export const TRANSLATION = {
    General: {
        Grid: "Netz",
        GridBuy: "Netzbezug",
        GridSell: "Netzeinspeisung",
        GridMode: "Keine Netzverbindung!",
        Production: "Erzeugung",
        Consumption: "Verbrauch",
        Power: "Leistung",
        StorageSystem: "Speichersystem",
        History: "Historie",
        NoValue: "Kein Wert",
        Soc: "Ladezustand",
        Percentage: "Prozent",
        More: "Mehr...",
        ChargePower: "Beladung",
        DischargePower: "Entladung",
        PeriodFromTo: "von {{value1}} bis {{value2}}", // value1 = start date, value2 = end date
        DateFormat: "dd.MM.yyyy", // z.B. Englisch: yyyy-MM-dd (dd = Tag, MM = Monat, yyyy = Jahr)
        Search: "Suchen",
        Week: {
            Monday: "Montag",
            Tuesday: "Dienstag",
            Wednesday: "Mittwoch",
            Thursday: "Donnerstag",
            Friday: "Freitag",
            Saturday: "Samstag",
            Sunday: "Sonntag"
        }
    },
    Menu: {
        Index: "Übersicht",
        AboutUI: "Über PRIMUS-UI",
        Settings: 'Allgemeine Einstellungen',
        Logout: 'Abmelden'
    },
    Index: {
        AllConnected: "Alle Verbindungen hergestellt.",
        ConnectionSuccessful: "Verbindung zu {{value}} hergestellt.", // value = name of websocket
        ConnectionFailed: "Verbindung zu {{value}} getrennt.", // value = name of websocket
        ToEnergymonitor: "Zum Energiemonitor...",
        IsOffline: "PRIMUS ist offline!",
        PleaseLogin: "Bitte geben Sie Ihre Zugangsdaten ein oder bestätigen Sie die Voreingabe um sich anzumelden.",
        Username: "Benutzername / Email",
        Password: "Passwort",
        LostPassword: "Passwort vergessen",
        FormInvalid: "Bitte füllen Sie das Formular komplett aus.",
        Connecting: "Verbindung wird aufgebaut.",
        LoginWrong: "Benutzername / Passwort falsch.",
        NotOnline: "Das Gerät ist nicht verbunden.",
        Type: "Typ:",
        ConnectedAs: "Angemeldet als:",
        MoreDevices: "Es sind weitere Geräte vorhanden... Bitte Filter enger setzen.",
        SaveLogin: "Login Daten speichern"
    },
    Edge: {
        Index: {
            Energymonitor: {
                Title: "Energiemonitor",
                ConsumptionWarning: "Verbrauch & unbekannte Erzeuger",
                Storage: "Speicher",
                ReactivePower: "Blindleistung",
                ActivePower: "Ausgabeleistung",
                GridMeter: "Netzzähler",
                ProductionMeter: "Erzeugungszähler",
                StorageDischarge: "Speicher-Entladung",
                StorageCharge: "Speicher-Beladung"
            },
            Energytable: {
                Title: "Energietabelle",
                LoadingDC: "Beladung DC",
                ProductionDC: "Erzeugung DC"
            },
            Widgets: {
                EVCS: {
                    ChargingStation: "Ladestation",
                    Status: "Status",
                    Starting: "Startet",
                    NotReadyForCharging: "Nicht bereit zur Beladung",
                    ReadyForCharging: "Bereit zur Beladung",
                    Charging: "Beladung läuft",
                    Error: "Fehler",
                    NotAuthorized: "Nicht authorisiert",
                    Unplugged: "Ausgesteckt",
                    ChargingStationPluggedIn: "Ladestation eingesteckt",
                    ChargingStationPluggedInLocked: "Ladestation eingesteckt + gesperrt",
                    ChargingStationPluggedInEV: "Ladestation + E-Auto eingesteckt",
                    ChargingStationPluggedInEVLocked: "Ladestation + E-Auto eingesteckt + gesperrt",
                    ChargingLimit: "Lade-Begrenzung",
                    ChargingPower: "Lade-Leistung",
                    CurrentCharge: "Aktuelle Beladung",
                    TotalCharge: "Gesamte Beladung",
                    EnforceCharging: "Erzwinge Beladung",
                    Cable: "Kabel"
                }
            }
        },
        History: {
            SelectedPeriod: "Gewählter Zeitraum: ",
            OtherPeriod: "Anderer Zeitraum",
            Period: "Zeitraum",
            Today: "Heute",
            Yesterday: "Gestern",
            LastWeek: "Letzte Woche",
            LastMonth: "Letzter Monat",
            LastYear: "Letztes Jahr",
            Go: "Los!",
            Cumulative: "Kumulierte Daten"
        },
        Config: {
            Index: {
                Bridge: "Verbindungen und Geräte",
                Scheduler: "Anwendungsplaner",
                Controller: "Anwendungen",
                Simulator: "Simulator",
                ExecuteSimulator: "Simulationen ausführen",
                Log: "Log",
                LiveLog: "Live Systemprotokoll",
                ManualControl: "Manuelle Steuerung",
                DataStorage: "Datenspeicher"
            },
            More: {
                ManualCommand: "Manueller Befehl",
                Send: "Senden",
                RefuInverter: "REFU Wechselrichter",
                RefuStartStop: "Wechselrichter starten/stoppen",
                RefuStart: "Starten",
                RefuStop: "Stoppen",
                ManualpqPowerSpecification: "Leistungsvorgabe",
                ManualpqSubmit: "Übernehmen",
                ManualpqReset: "Zurücksetzen"
            },
            Scheduler: {
                NewScheduler: "Neuer Scheduler...",
                Class: "Klasse:",
                NotImplemented: "Formular nicht implementiert: ",
                Contact: "Das sollte nicht passieren. Bitte kontaktieren Sie <a href=\"mailto:{{value}}\">{{value}}</a>.",
                Always: "Immer"
            },
            Log: {
                AutomaticUpdating: "Automatische Aktualisierung",
                Timestamp: "Zeitpunkt",
                Level: "Level",
                Source: "Quelle",
                Message: "Nachricht"
            },
            Controller: {
                InternallyID: "Interne ID:",
                App: "Anwendung:",
                Priority: "Priorität:"
            },
            Bridge: {
                NewDevice: "Neues Gerät...",
                NewConnection: "Neue Verbindung..."
            }
        }
    },
    About: {
        UI: "Benutzeroberfläche OpenEMS",
        Developed: "Diese Benutzeroberfläche wird von als Open-Source-Software entwickelt.",
        OpenEMS: "Mehr zu OpenEMS",
        CurrentDevelopments: "Aktuelle Entwicklungen",
        Build: "Dieser Build",
        Contact: "Für Rückfragen und Anregungen zum System, wenden Sie sich bitte an unser Team unter <a href=\"mailto:{{value}}\">{{value}}</a>.",
        Language: "Sprache wählen:"
    },
    Notifications: {
        Failed: "Verbindungsaufbau fehlgeschlagen.",
        LoggedInAs: "Angemeldet als Benutzer \"{{value}}\".", // value = username
        LoggedIn: "Angemeldet.",
        AuthenticationFailed: "Keine Verbindung: Authentifizierung fehlgeschlagen.",
        Closed: "Verbindung beendet."
    },
    Alerts: {
        Error: "Fehler",
        Default: "Etwas is schief gelaufen. Bitte versuchen Sie es noch einmal.",
        RetrievePwdHeader: "Passwort vergessen",
        RetrievePwdMsg: "Bitte geben Sie Ihre(n) Email / Benutzernamen um ein neues Passwort zu erstellen.",
        RetrievePwdPlaceholder: "Email/Benutzername",
        Cancel: "Abbrechen",
        Send: "Senden",
        RetrievePwdSent: "Ein Link zur Wiederherstellung Ihres Passworts wurde an Sie gesendet. Bitte überprüfen Sie Ihre Emails.",
        RetrievePwdError: "Bei der Wiederherstellung Ihres Passworts is ein Fehler aufgetreten. Bitte überprüfen Sie Ihre Eingabe und versuchen es erneut.",
        ClearLoginHeader: "Login Daten löschen",
        ClearLoginMsg: "Wenn Sie Ihre Login Daten löschen, müssen Sie Ihren Benutzernamen und Ihr Passwort bei der nächsten Anmeldung erneut eingeben. Jetz Login Daten löschen?",
        ClearLoginDone: "Login Daten gelöscht."
    }
}