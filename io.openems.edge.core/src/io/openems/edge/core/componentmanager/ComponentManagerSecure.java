package io.openems.edge.core.componentmanager;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.openems.common.OpenemsConstants;
import io.openems.common.channel.Level;
import io.openems.common.exceptions.OpenemsError;
import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.jsonrpc.base.GenericJsonrpcResponseSuccess;
import io.openems.common.jsonrpc.base.JsonrpcRequest;
import io.openems.common.jsonrpc.base.JsonrpcResponseSuccess;
import io.openems.common.jsonrpc.request.CreateComponentConfigRequest;
import io.openems.common.jsonrpc.request.DeleteComponentConfigRequest;
import io.openems.common.jsonrpc.request.GetEdgeConfigRequest;
import io.openems.common.jsonrpc.request.UpdateComponentConfigRequest;
import io.openems.common.jsonrpc.request.UpdateComponentConfigRequest.Property;
import io.openems.common.jsonrpc.response.GetEdgeConfigResponse;
import io.openems.common.session.Role;
import io.openems.common.session.User;
import io.openems.common.types.ChannelAddress;
import io.openems.common.types.EdgeConfig;
import io.openems.common.types.EdgeConfig.Component.Channel.ChannelDetail;
import io.openems.common.types.EdgeConfig.Component.Channel.ChannelDetailOpenemsType;
import io.openems.common.types.EdgeConfig.Component.Channel.ChannelDetailState;
import io.openems.common.types.OptionsEnum;
import io.openems.common.utils.FileUtils;
import io.openems.common.utils.JsonKeys;
import io.openems.common.utils.JsonUtils;
import io.openems.edge.common.access_control.AccessControl;
import io.openems.edge.common.access_control.Group;
import io.openems.edge.common.access_control.Permission;
import io.openems.edge.common.channel.*;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.jsonapi.JsonApi;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.MetaTypeInformation;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.service.metatype.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

@Designate(
        ocd = Config.class, factory = false
)
@Component( //
        name = "Core.ComponentManagerSecure", //
        configurationPolicy = ConfigurationPolicy.REQUIRE)
public class ComponentManagerSecure extends AbstractOpenemsComponent
        implements ComponentManager, OpenemsComponent, JsonApi, ConfigurationListener {

    private final Logger log = LoggerFactory.getLogger(ComponentManagerSecure.class);

    private final OsgiValidateWorker osgiValidateWorker;

    private BundleContext bundleContext;

    private String path = "";
    private AccessControl accessControl = new AccessControl();

    @Reference
    private MetaTypeService metaTypeService;

    @Reference
    protected ConfigurationAdmin cm;

    @Reference(policy = ReferencePolicy.DYNAMIC, //
            policyOption = ReferencePolicyOption.GREEDY, //
            cardinality = ReferenceCardinality.MULTIPLE, //
            target = "(&(enabled=true)(!(service.factoryPid=Core.ComponentManager)))")
    protected volatile List<OpenemsComponent> components = new CopyOnWriteArrayList<>();

    public ComponentManagerSecure() {
        super(//
                OpenemsComponent.ChannelId.values(), //
                ComponentManager.ChannelId.values() //
        );
        this.osgiValidateWorker = new OsgiValidateWorker(this);
    }

    @Activate
    void activate(ComponentContext componentContext, BundleContext bundleContext, Config config) {
        super.activate(componentContext, OpenemsConstants.COMPONENT_MANAGER_ID, true);

        this.path = config.path();
        this.bundleContext = bundleContext;

        // Start OSGi Validate Worker
        this.osgiValidateWorker.activate(this.id());

        this.initializeAccessControl();
    }

    @Deactivate
    protected void deactivate() {
        super.deactivate();

        // Stop OSGi Validate Worker
        this.osgiValidateWorker.deactivate();
    }

    private void initializeAccessControl() {
        StringBuilder sb = FileUtils.checkAndGetFileContent(this.path);
        if (sb == null) {
            // exception occurred. File could not be read
            return;
        }

        try {
            JsonElement config = JsonUtils.parse(sb.toString());
            handleGroups(config);
            handleRoles(config);
            handleUsers(config);
        } catch (OpenemsNamedException e) {
            this.logWarn(this.log, "Unable to parse JSON-file [" + this.path + "]: " + e.getMessage());
        }
    }

    private void handleUsers(JsonElement config) throws OpenemsNamedException {
        for (JsonElement userJson : JsonUtils.getAsJsonArray(config, JsonKeys.USERS.getValue())) {
            io.openems.edge.common.access_control.User newUser = new io.openems.edge.common.access_control.User();
            newUser.setId(JsonUtils.getAsLong(userJson, JsonKeys.ID.getValue()));
            newUser.setUsername(JsonUtils.getAsString(userJson, JsonKeys.NAME.getValue()));
            newUser.setDescription(JsonUtils.getAsString(userJson, JsonKeys.DESCRIPTION.getValue()));
            newUser.setPassword(JsonUtils.getAsString(userJson, JsonKeys.PASSWORD.getValue()));
            newUser.setEmail(JsonUtils.getAsString(userJson, JsonKeys.EMAIL.getValue()));
            newUser.setRoles(accessControl.getRoles());
            accessControl.addUser(newUser);
        }
    }

    private void handleRoles(JsonElement config) throws OpenemsNamedException {
        for (JsonElement roleJson : JsonUtils.getAsJsonArray(config, JsonKeys.ROLES.getValue())) {
            io.openems.edge.common.access_control.Role newRole = new io.openems.edge.common.access_control.Role();
            newRole.setId(JsonUtils.getAsLong(roleJson, JsonKeys.ID.getValue()));
            newRole.setDescription(JsonUtils.getAsString(roleJson, JsonKeys.DESCRIPTION.getValue()));
            newRole.setName(JsonUtils.getAsString(roleJson, JsonKeys.NAME.getValue()));
            Set<Long> groupIds = new HashSet<>();
            for (JsonElement jsonElement : JsonUtils.getAsJsonArray(roleJson, JsonKeys.ASSIGNED_TO_GROUPS.getValue())) {
                groupIds.add(jsonElement.getAsLong());
            }

            newRole.setGroups(accessControl.getGroups().stream().filter(
                    group -> groupIds.contains(group.getId())).collect(Collectors.toSet()));
            accessControl.addRole(newRole);
        }
    }

    private void handleGroups(JsonElement config) throws OpenemsNamedException {
        for (JsonElement group : JsonUtils.getAsJsonArray(config, JsonKeys.GROUPS.getValue())) {
            Group newGroup = new Group();
            newGroup.setId(JsonUtils.getAsLong(group, JsonKeys.ID.getValue()));
            newGroup.setName(JsonUtils.getAsString(group, JsonKeys.NAME.getValue()));
            newGroup.setDescription(JsonUtils.getAsString(group, JsonKeys.DESCRIPTION.getValue()));
            Map<ChannelAddress, Set<Permission>> mapping = new HashMap<>();
            for (JsonElement jsonPer : JsonUtils.getAsJsonArray(group, JsonKeys.PERMISSIONS.getValue())) {
                ChannelAddress channelAddress = new ChannelAddress(
                        JsonUtils.getAsString(jsonPer, JsonKeys.COMPONENT_ID.getValue()),
                        JsonUtils.getAsString(jsonPer, JsonKeys.CHANNEL_ID.getValue()));
                Set<Permission> permissions = new HashSet<>();
                for (JsonElement jsonElement : JsonUtils.getAsJsonArray(jsonPer, JsonKeys.PERMISSION.getValue())) {
                    permissions.add(Permission.from(JsonUtils.getAsString(jsonElement)));
                }
                mapping.put(channelAddress, permissions);
            }
            newGroup.setChannelToPermissionsMapping(mapping);
            accessControl.addGroup(newGroup);
        }
    }

    @Override
    public List<OpenemsComponent> getComponents() {
        return Collections.unmodifiableList(this.components);
    }

    /**
     * Returns all components which are accessible with this role
     *
     * @param role
     * @return
     */
    public List<OpenemsComponent> getComponentValues(Role role) {
        return null;
    }

    protected StateChannel configNotActivatedChannel() {
        return this.channel(ComponentManager.ChannelId.CONFIG_NOT_ACTIVATED);
    }

    @Override
    public void logWarn(Logger log, String message) {
        super.logWarn(log, message);
    }

    @Override
    public void logError(Logger log, String message) {
        super.logError(log, message);
    }

    @Override
    public List<String> checkForNotActivatedComponents() {
        // TODO use as default in interface
        List<String> retVal = new ArrayList<>();
        try {
            Configuration[] configs = cm.listConfigurations("(enabled=true)");
            if (configs != null) {
                Arrays.stream(configs).forEach(config -> {
                    String componentId = (String) config.getProperties().get("id");
                    if (!this.isComponentActivated(componentId, config.getPid())) {
                        retVal.add(componentId);
                    }
                });
            }
        } catch (IOException | InvalidSyntaxException e) {
            this.logError(this.log, e.getMessage());
        }
        return retVal;
    }

    @Override
    public CompletableFuture<JsonrpcResponseSuccess> handleJsonrpcRequest(User user, JsonrpcRequest request)
            throws OpenemsNamedException {
        user.assertRoleIsAtLeast("handleJsonrpcRequest", Role.GUEST);

        switch (request.getMethod()) {

            case GetEdgeConfigRequest.METHOD:
                return this.handleGetEdgeConfigRequest(user, GetEdgeConfigRequest.from(request));

            case CreateComponentConfigRequest.METHOD:
                return this.handleCreateComponentConfigRequest(user, CreateComponentConfigRequest.from(request));

            case UpdateComponentConfigRequest.METHOD:
                return this.handleUpdateComponentConfigRequest(user, UpdateComponentConfigRequest.from(request));

            case DeleteComponentConfigRequest.METHOD:
                return this.handleDeleteComponentConfigRequest(user, DeleteComponentConfigRequest.from(request));

            default:
                throw OpenemsError.JSONRPC_UNHANDLED_METHOD.exception(request.getMethod());
        }
    }

    /**
     * Handles a GetEdgeConfigRequest.
     *
     * @param user    the User
     * @param request the GetEdgeConfigRequest
     * @return the Future JSON-RPC Response
     * @throws OpenemsNamedException on error
     */
    private CompletableFuture<JsonrpcResponseSuccess> handleGetEdgeConfigRequest(User user,
                                                                                 GetEdgeConfigRequest request) throws OpenemsNamedException {
        EdgeConfig config = this.getEdgeConfig();
        GetEdgeConfigResponse response = new GetEdgeConfigResponse(request.getId(), config);
        return CompletableFuture.completedFuture(response);
    }

    /**
     * Handles a CreateComponentConfigRequest.
     *
     * @param user    the User
     * @param request the CreateComponentConfigRequest
     * @return the Future JSON-RPC Response
     * @throws OpenemsNamedException on error
     */
    private CompletableFuture<JsonrpcResponseSuccess> handleCreateComponentConfigRequest(User user,
                                                                                         CreateComponentConfigRequest request) throws OpenemsNamedException {
        Configuration config;
        try {
            config = this.cm.createFactoryConfiguration(request.getFactoryPid(), null);
        } catch (IOException e) {
            e.printStackTrace();
            throw OpenemsError.GENERIC.exception("Unable create Configuration for Factory-ID ["
                    + request.getFactoryPid() + "]. " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        // Create map with configuration attributes
        Dictionary<String, Object> properties = new Hashtable<>();
        for (Property property : request.getProperties()) {
            properties.put(property.getName(), JsonUtils.getAsBestType(property.getValue()));
        }

        // Update Configuration
        try {
            this.applyConfiguration(user, config, properties);
        } catch (IOException e) {
            e.printStackTrace();
            throw OpenemsError.EDGE_UNABLE_TO_CREATE_CONFIG.exception(request.getFactoryPid(), e.getMessage());
        }

        return CompletableFuture.completedFuture(new GenericJsonrpcResponseSuccess(request.getId()));
    }

    /**
     * Handles a UpdateComponentConfigRequest.
     *
     * @param user    the User
     * @param request the UpdateComponentConfigRequest
     * @return the Future JSON-RPC Response
     * @throws OpenemsNamedException on error
     */
    private CompletableFuture<JsonrpcResponseSuccess> handleUpdateComponentConfigRequest(User user,
                                                                                         UpdateComponentConfigRequest request) throws OpenemsNamedException {
        Configuration config = this.getExistingConfigForId(request.getComponentId());

        // Create map with changed configuration attributes
        Dictionary<String, Object> properties = config.getProperties();
        for (Property property : request.getProperties()) {
            // do not allow certain properties to be updated, like pid and service.pid
            if (!this.ignorePropertyKey(property.getName())) {
                Object value = JsonUtils.getAsBestType(property.getValue());
                if (value instanceof Object[] && ((Object[]) value).length == 0) {
                    value = new String[0];
                }
                properties.put(property.getName(), value);
            }
        }

        // Update Configuration
        try {
            this.applyConfiguration(user, config, properties);
        } catch (IOException e) {
            e.printStackTrace();
            throw OpenemsError.EDGE_UNABLE_TO_APPLY_CONFIG.exception(request.getComponentId(), e.getMessage());
        }

        return CompletableFuture.completedFuture(new GenericJsonrpcResponseSuccess(request.getId()));
    }

    /**
     * Handles a DeleteComponentConfigRequest.
     *
     * @param user    the User
     * @param request the DeleteComponentConfigRequest
     * @return the Future JSON-RPC Response
     * @throws OpenemsNamedException on error
     */
    private CompletableFuture<JsonrpcResponseSuccess> handleDeleteComponentConfigRequest(User user,
                                                                                         DeleteComponentConfigRequest request) throws OpenemsNamedException {
        Configuration config = this.getExistingConfigForId(request.getComponentId());

        try {
            config.delete();
        } catch (IOException e) {
            e.printStackTrace();
            throw OpenemsError.EDGE_UNABLE_TO_DELETE_CONFIG.exception(request.getComponentId(), e.getMessage());
        }

        return CompletableFuture.completedFuture(new GenericJsonrpcResponseSuccess(request.getId()));
    }

    /**
     * Updates the Configuration from the given Properties and adds some meta
     * information.
     *
     * @param user       the User
     * @param config     the Configuration object
     * @param properties the properties
     * @throws IOException on error
     */
    private void applyConfiguration(User user, Configuration config, Dictionary<String, Object> properties)
            throws IOException {
        properties.put(OpenemsConstants.PROPERTY_LAST_CHANGE_BY, user.getId() + ": " + user.getName());
        properties.put(OpenemsConstants.PROPERTY_LAST_CHANGE_AT,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
        config.update(properties);
    }

    /**
     * Gets the ConfigAdmin Configuration for the OpenEMS Component with the given
     * Component-ID.
     *
     * @param componentId the Component-ID
     * @return the Configuration
     * @throws OpenemsNamedException on error
     */
    private Configuration getExistingConfigForId(String componentId) throws OpenemsNamedException {
        Configuration[] configs;
        try {
            configs = this.cm.listConfigurations("(id=" + componentId + ")");
        } catch (IOException | InvalidSyntaxException e) {
            e.printStackTrace();
            throw OpenemsError.GENERIC.exception("Unable to list configurations for ID [" + componentId + "]. "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        // Make sure we only have one config
        if (configs == null || configs.length == 0) {
            throw OpenemsError.EDGE_NO_COMPONENT_WITH_ID.exception(componentId);
        } else if (configs.length > 1) {
            throw OpenemsError.EDGE_MULTIPLE_COMPONENTS_WITH_ID.exception(componentId);
        }
        return configs[0];
    }

    @Override
    public EdgeConfig getEdgeConfig() {
        EdgeConfig result = new EdgeConfig();

        // get configurations that have an 'id' property -> OpenEMS Components
        try {
            Configuration[] configs = this.cm.listConfigurations("(id=*)");
            for (Configuration config : configs) {
                Dictionary<String, Object> properties = config.getProperties();
                String componentId = properties.get("id").toString();
                // get Factory-PID
                Object factoryPid = config.getFactoryPid();
                if (factoryPid == null) {
                    this.logWarn(this.log, "Component [" + componentId + "] has no Factory-PID");
                    continue;
                }
                // get configuration properties
                TreeMap<String, JsonElement> propertyMap = new TreeMap<>();
                Enumeration<String> keys = properties.keys();
                while (keys.hasMoreElements()) {
                    String key = keys.nextElement();
                    if (!this.ignorePropertyKey(key)) {
                        propertyMap.put(key, JsonUtils.getAsJsonElement(properties.get(key)));
                    }
                }
                // get Channels
                TreeMap<String, EdgeConfig.Component.Channel> channelMap = new TreeMap<>();
                try {
                    OpenemsComponent component = this.getComponent(componentId);
                    for (Channel<?> channel : component.channels()) {
                        io.openems.edge.common.channel.ChannelId channelId = channel.channelId();
                        Doc doc = channelId.doc();
                        ChannelDetail detail = null;
                        switch (doc.getChannelCategory()) {
                            case ENUM: {
                                Map<String, JsonElement> values = new HashMap<>();
                                EnumDoc d = (EnumDoc) doc;
                                for (OptionsEnum option : d.getOptions()) {
                                    values.put(option.getName(), new JsonPrimitive(option.getValue()));
                                }
                                detail = new EdgeConfig.Component.Channel.ChannelDetailEnum(values);
                                break;
                            }
                            case OPENEMS_TYPE:
                                detail = new ChannelDetailOpenemsType();
                                break;
                            case STATE:
                                StateChannelDoc d = (StateChannelDoc) doc;
                                Level level = d.getLevel();
                                detail = new ChannelDetailState(level);
                                break;
                        }
                        channelMap.put(channelId.id(), new EdgeConfig.Component.Channel(//
                                doc.getType(), //
                                doc.getAccessMode(), //
                                doc.getUnit(), //
                                detail //
                        ));
                    }
                } catch (OpenemsNamedException e) {
                    // Component not found. Ignore and return empty Channel-Map
                    this.logWarn(this.log, e.getMessage());
                }

                result.addComponent(componentId,
                        new EdgeConfig.Component(factoryPid.toString(), propertyMap, channelMap));
            }
        } catch (IOException | InvalidSyntaxException e) {
            this.logWarn(this.log,
                    "Unable to list configurations " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        final Bundle[] bundles = this.bundleContext.getBundles();
        for (Bundle bundle : bundles) {
            final MetaTypeInformation mti = this.metaTypeService.getMetaTypeInformation(bundle);

            // read Bundle Manifest
            URL manifestUrl = bundle.getResource("META-INF/MANIFEST.MF");
            Manifest manifest;
            try {
                manifest = new Manifest(manifestUrl.openStream());
            } catch (IOException e) {
                // unable to read manifest
                continue;
            }

            // get Factory-PIDs in this Bundle
            String[] factoryPids = mti.getFactoryPids();
            for (String factoryPid : factoryPids) {
                switch (factoryPid) {
                    case "osgi.executor.provider":
                        // ignore these Factory-PIDs
                        break;
                    default:
                        // Get ObjectClassDefinition (i.e. the main annotation on the Config class)
                        ObjectClassDefinition objectClassDefinition = mti.getObjectClassDefinition(factoryPid, null);
                        // Get Natures implemented by this Factory-PID
                        String[] natures = this.getNatures(bundle, manifest, factoryPid);
                        // Add Factory to config
                        result.addFactory(factoryPid, EdgeConfig.Factory.create(objectClassDefinition, natures));
                }
            }
        }
        return result;
    }

    /**
     * Reads Natures from an XML:
     *
     * <pre>
     * <scr:component>
     *   <service>
     *     <provide interface="...">
     *   </service>
     * </scr:component>
     * </pre>
     *
     * @return
     */
    private String[] getNatures(Bundle bundle, Manifest manifest, String factoryPid) {
        try {
            // get "Service-Component"-Entry of Manifest
            String serviceComponentsString = manifest.getMainAttributes()
                    .getValue(ComponentConstants.SERVICE_COMPONENT);
            if (serviceComponentsString == null) {
                return new String[0];
            }
            String[] serviceComponents = serviceComponentsString.split(",");

            // read Service-Component XML files from OSGI-INF folder
            for (String serviceComponent : serviceComponents) {
                if (!serviceComponent.contains(factoryPid)) {
                    // search for correct XML file
                    continue;
                }

                URL componentUrl = bundle.getResource(serviceComponent);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(componentUrl.openStream());
                doc.getDocumentElement().normalize();

                NodeList serviceNodes = doc.getElementsByTagName("service");
                for (int i = 0; i < serviceNodes.getLength(); i++) {
                    Node serviceNode = serviceNodes.item(i);
                    if (serviceNode.getNodeType() == Node.ELEMENT_NODE) {
                        NodeList provideNodes = serviceNode.getChildNodes();

                        // Read "interface" attributes and return them
                        Set<String> result = new HashSet<>();
                        for (int j = 0; j < provideNodes.getLength(); j++) {
                            Node provideNode = provideNodes.item(j);
                            NamedNodeMap attributes = provideNode.getAttributes();
                            if (attributes != null) {
                                Node interfaceNode = attributes.getNamedItem("interface");
                                String nature = interfaceNode.getNodeValue();
                                switch (nature) {
                                    case "org.osgi.service.event.EventHandler":
                                    case "org.ops4j.pax.logging.spi.PaxAppender":
                                        // ignore these natures;
                                        break;
                                    default:
                                        result.add(nature);
                                }
                            }
                        }
                        return result.toArray(new String[result.size()]);
                    }
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            this.logWarn(this.log, "Unable to get Natures. " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        return new String[0];
    }

    @Override
    public void configurationEvent(ConfigurationEvent event) {
        // trigger immediate validation on configuration event
        this.osgiValidateWorker.triggerNextRun();
    }

    /**
     * Internal Method to decide whether a configuration property should be ignored.
     *
     * @param key the property key
     * @return true if it should get ignored
     */
    private boolean ignorePropertyKey(String key) {
        if (key.endsWith(".target")) {
            return true;
        }
        switch (key) {
            case OpenemsConstants.PROPERTY_COMPONENT_ID:
            case OpenemsConstants.PROPERTY_OSGI_COMPONENT_ID:
            case OpenemsConstants.PROPERTY_OSGI_COMPONENT_NAME:
            case OpenemsConstants.PROPERTY_FACTORY_PID:
            case OpenemsConstants.PROPERTY_PID:
            case "webconsole.configurationFactory.nameHint":
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean isComponentActivated(String componentId, String pid) {
        return components.stream().anyMatch(
                com -> (componentId.equals(com.id()) && pid.equals(com.servicePid())));
    }
}
