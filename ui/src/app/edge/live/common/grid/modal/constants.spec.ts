import { TextIndentation } from "../../../../../shared/genericComponents/modal/modal-line/modal-line";
import { OeFormlyViewTester } from "../../../../../shared/genericComponents/shared/tester";

export namespace Constants {

  export const EMS1_ADMIN_AND_INSTALLER: OeFormlyViewTester.View = {
    title: "Netz",
    lines: [
      {
        type: "line",
        name: "Bezug",
        value: "0 W"
      }, {
        type: "line",
        name: "Einspeisung",
        value: "1.000 W"
      }, {
        type: "line",
        name: "Phase L1 Einspeisung",
        value: "-500",
        indentation: TextIndentation.SINGLE,
        children: [{
          type: "line-item",
          value: "230 V"
        }, {
          type: "line-item",
          value: "2,2 A"
        }, {
          type: "line-item",
          value: "500 W"
        }]
      }, {
        type: "line",
        name: "Phase L2 Bezug",
        value: "1500",
        indentation: TextIndentation.SINGLE,
        children: [{
          type: "line-item",
          value: "-"
        }, {
          type: "line-item",
          value: "-"
        }, {
          type: "line-item",
          value: "1.500 W"
        }]
      }, {
        type: "line",
        name: "Phase L3",
        indentation: TextIndentation.SINGLE,
        children: [{
          type: "line-item",
          value: "-"
        }, {
          type: "line-item",
          value: "-"
        }, {
          type: "line-item",
          value: "0 W"
        }]
      }, {
        type: "line-horizontal"
      }, {
        type: "line-info",
        name: "Die Summe der einzelnen Phasen kann aus technischen Gründen geringfügig von der Gesamtsumme abweichen."
      }
    ]
  };
}

// export const EMS1_OWNER_AND_GUEST: OeFormlyView = {
//   "title": "Netz",
//   "lines": [
//     {
//       "type": "line",
//       "name": "Keine Netzverbindung!",
//       "channel": "_sum/GridMode"
//     },
//     {
//       "type": "line",
//       "name": "Bezug",
//       "channel": "meter0/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Einspeisung",
//       "channel": "meter0/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Phase 1 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL1",
//       "children": [
//         undefined,
//         undefined,
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL1"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 2 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL2",
//       "children": [
//         undefined,
//         undefined,
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL2"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 3 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL3",
//       "children": [
//         undefined,
//         undefined,
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL3"
//         }
//       ]
//     },
//     {
//       "type": "line-horizontal"
//     },
//     {
//       "type": "line-info",
//       "name": "Die Summe der einzelnen Phasen kann aus technischen Gründen geringfügig von der Gesamtsumme abweichen."
//     }
//   ]
// };

// export const EMS4_OWNER_AND_GUEST: OeFormlyView = {
//   "title": "Netz",
//   "lines": [
//     {
//       "type": "line",
//       "name": "Keine Netzverbindung!",
//       "channel": "_sum/GridMode"
//     },
//     {
//       "type": "line",
//       "name": "Bezug",
//       "channel": "meter1/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Einspeisung",
//       "channel": "meter1/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Phase 1 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter1/ActivePowerL1",
//       "children": [
//         undefined,
//         undefined,
//         {
//           "type": "line-item",
//           "channel": "meter1/ActivePowerL1"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 2 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter1/ActivePowerL2",
//       "children": [
//         undefined,
//         undefined,
//         {
//           "type": "line-item",
//           "channel": "meter1/ActivePowerL2"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 3 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter1/ActivePowerL3",
//       "children": [
//         undefined,
//         undefined,
//         {
//           "type": "line-item",
//           "channel": "meter1/ActivePowerL3"
//         }
//       ]
//     },
//     {
//       "type": "line-horizontal"
//     },
//     {
//       "type": "line-info",
//       "name": "Die Summe der einzelnen Phasen kann aus technischen Gründen geringfügig von der Gesamtsumme abweichen."
//     }
//   ]
// };

// export const EMS4_ADMIN_AND_INSTALLER: OeFormlyView = {
//   "title": "Netz",
//   "lines": [
//     {
//       "type": "line",
//       "name": "Keine Netzverbindung!",
//       "channel": "_sum/GridMode"
//     },
//     {
//       "type": "line",
//       "name": "Bezug",
//       "channel": "meter1/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Einspeisung",
//       "channel": "meter1/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Phase 1 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter1/ActivePowerL1",
//       "children": [
//         {
//           "type": "line-item",
//           "channel": "meter1/VoltageL1"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter1/CurrentL1"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter1/ActivePowerL1"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 2 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter1/ActivePowerL2",
//       "children": [
//         {
//           "type": "line-item",
//           "channel": "meter1/VoltageL2"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter1/CurrentL2"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter1/ActivePowerL2"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 3 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter1/ActivePowerL3",
//       "children": [
//         {
//           "type": "line-item",
//           "channel": "meter1/VoltageL3"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter1/CurrentL3"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter1/ActivePowerL3"
//         }
//       ]
//     },
//     {
//       "type": "line-horizontal"
//     },
//     {
//       "type": "line-info",
//       "name": "Die Summe der einzelnen Phasen kann aus technischen Gründen geringfügig von der Gesamtsumme abweichen."
//     }
//   ]
// };

// export const EMS10004_OWNER_AND_GUEST: OeFormlyView = {
//   "title": "Netz",
//   "lines": [
//     {
//       "type": "line",
//       "name": "Keine Netzverbindung!",
//       "channel": "_sum/GridMode"
//     },
//     {
//       "type": "line",
//       "name": "Bezug",
//       "channel": "meter0/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Einspeisung",
//       "channel": "meter0/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Phase 1 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL1",
//       "children": [
//         undefined,
//         undefined,
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL1"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 2 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL2",
//       "children": [
//         undefined,
//         undefined,
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL2"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 3 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL3",
//       "children": [
//         undefined,
//         undefined,
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL3"
//         }
//       ]
//     },
//     {
//       "type": "line-horizontal"
//     },
//     {
//       "type": "line-info",
//       "name": "Die Summe der einzelnen Phasen kann aus technischen Gründen geringfügig von der Gesamtsumme abweichen."
//     }
//   ]
// };

// export const EMS10004_ADMIN_AND_INSTALLER: OeFormlyView = {
//   "title": "Netz",
//   "lines": [
//     {
//       "type": "line",
//       "name": "Keine Netzverbindung!",
//       "channel": "_sum/GridMode"
//     },
//     {
//       "type": "line",
//       "name": "Bezug",
//       "channel": "meter0/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Einspeisung",
//       "channel": "meter0/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Phase 1 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL1",
//       "children": [
//         {
//           "type": "line-item",
//           "channel": "meter0/VoltageL1"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/CurrentL1"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL1"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 2 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL2",
//       "children": [
//         {
//           "type": "line-item",
//           "channel": "meter0/VoltageL2"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/CurrentL2"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL2"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 3 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL3",
//       "children": [
//         {
//           "type": "line-item",
//           "channel": "meter0/VoltageL3"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/CurrentL3"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL3"
//         }
//       ]
//     },
//     {
//       "type": "line-horizontal"
//     },
//     {
//       "type": "line-info",
//       "name": "Die Summe der einzelnen Phasen kann aus technischen Gründen geringfügig von der Gesamtsumme abweichen."
//     }
//   ]
// };

// export const EMS12786_OWNER_AND_GUEST: OeFormlyView = {
//   "title": "Netz",
//   "lines": [
//     {
//       "type": "line",
//       "name": "Keine Netzverbindung!",
//       "channel": "_sum/GridMode"
//     },
//     {
//       "type": "line",
//       "name": "Bezug",
//       "channel": "meter0/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Einspeisung",
//       "channel": "meter0/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Phase 1 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL1",
//       "children": [
//         undefined,
//         undefined,
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL1"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 2 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL2",
//       "children": [
//         undefined,
//         undefined,
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL2"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 3 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL3",
//       "children": [
//         undefined,
//         undefined,
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL3"
//         }
//       ]
//     },
//     {
//       "type": "line-horizontal"
//     },
//     {
//       "type": "line-info",
//       "name": "Die Summe der einzelnen Phasen kann aus technischen Gründen geringfügig von der Gesamtsumme abweichen."
//     }
//   ]
// }

// export const EMS12786_ADMIN_AND_INSTALLER: OeFormlyView = {
//   "title": "Netz",
//   "lines": [
//     {
//       "type": "line",
//       "name": "Keine Netzverbindung!",
//       "channel": "_sum/GridMode"
//     },
//     {
//       "type": "line",
//       "name": "Bezug",
//       "channel": "meter0/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Einspeisung",
//       "channel": "meter0/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Phase 1 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL1",
//       "children": [
//         {
//           "type": "line-item",
//           "channel": "meter0/VoltageL1"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/CurrentL1"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL1"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 2 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL2",
//       "children": [
//         {
//           "type": "line-item",
//           "channel": "meter0/VoltageL2"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/CurrentL2"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL2"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 3 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL3",
//       "children": [
//         {
//           "type": "line-item",
//           "channel": "meter0/VoltageL3"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/CurrentL3"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL3"
//         }
//       ]
//     },
//     {
//       "type": "line-horizontal"
//     },
//     {
//       "type": "line-info",
//       "name": "Die Summe der einzelnen Phasen kann aus technischen Gründen geringfügig von der Gesamtsumme abweichen."
//     }
//   ]
// }

// export const EMS30012_OWNER_AND_GUEST: OeFormlyView = {
//   "title": "Netz",
//   "lines": [
//     {
//       "type": "line",
//       "name": "Keine Netzverbindung!",
//       "channel": "_sum/GridMode"
//     },
//     {
//       "type": "line",
//       "name": "Bezug",
//       "channel": "meter0/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Einspeisung",
//       "channel": "meter0/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Phase 1 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL1",
//       "children": [
//         undefined,
//         undefined,
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL1"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 2 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL2",
//       "children": [
//         undefined,
//         undefined,
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL2"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 3 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL3",
//       "children": [
//         undefined,
//         undefined,
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL3"
//         }
//       ]
//     },
//     {
//       "type": "line-horizontal"
//     },
//     {
//       "type": "line-info",
//       "name": "Die Summe der einzelnen Phasen kann aus technischen Gründen geringfügig von der Gesamtsumme abweichen."
//     }
//   ]
// }

// export const EMS30012_ADMIN_AND_INSTALLER: OeFormlyView = {
//   "title": "Netz",
//   "lines": [
//     {
//       "type": "line",
//       "name": "Keine Netzverbindung!",
//       "channel": "_sum/GridMode"
//     },
//     {
//       "type": "line",
//       "name": "Bezug",
//       "channel": "meter0/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Einspeisung",
//       "channel": "meter0/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Phase 1 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL1",
//       "children": [
//         {
//           "type": "line-item",
//           "channel": "meter0/VoltageL1"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/CurrentL1"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL1"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 2 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL2",
//       "children": [
//         {
//           "type": "line-item",
//           "channel": "meter0/VoltageL2"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/CurrentL2"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL2"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 3 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL3",
//       "children": [
//         {
//           "type": "line-item",
//           "channel": "meter0/VoltageL3"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/CurrentL3"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL3"
//         }
//       ]
//     },
//     {
//       "type": "line-horizontal"
//     },
//     {
//       "type": "line-info",
//       "name": "Die Summe der einzelnen Phasen kann aus technischen Gründen geringfügig von der Gesamtsumme abweichen."
//     }
//   ]
// }

// export const EMS30034_OWNER_AND_GUEST: OeFormlyView = {
//   "title": "Netz",
//   "lines": [
//     {
//       "type": "line",
//       "name": "Keine Netzverbindung!",
//       "channel": "_sum/GridMode"
//     },
//     {
//       "type": "line",
//       "name": "Bezug",
//       "channel": "meter0/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Einspeisung",
//       "channel": "meter0/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Phase 1 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL1",
//       "children": [
//         undefined,
//         undefined,
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL1"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 2 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL2",
//       "children": [
//         undefined,
//         undefined,
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL2"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 3 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL3",
//       "children": [
//         undefined,
//         undefined,
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL3"
//         }
//       ]
//     },
//     {
//       "type": "line-horizontal"
//     },
//     {
//       "type": "line-info",
//       "name": "Die Summe der einzelnen Phasen kann aus technischen Gründen geringfügig von der Gesamtsumme abweichen."
//     }
//   ]
// }

// export const EMS30034_ADMIN_AND_INSTALLER: OeFormlyView = {
//   "title": "Netz",
//   "lines": [
//     {
//       "type": "line",
//       "name": "Keine Netzverbindung!",
//       "channel": "_sum/GridMode"
//     },
//     {
//       "type": "line",
//       "name": "Bezug",
//       "channel": "meter0/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Einspeisung",
//       "channel": "meter0/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Phase 1 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL1",
//       "children": [
//         {
//           "type": "line-item",
//           "channel": "meter0/VoltageL1"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/CurrentL1"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL1"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 2 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL2",
//       "children": [
//         {
//           "type": "line-item",
//           "channel": "meter0/VoltageL2"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/CurrentL2"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL2"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 3 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL3",
//       "children": [
//         {
//           "type": "line-item",
//           "channel": "meter0/VoltageL3"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/CurrentL3"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL3"
//         }
//       ]
//     },
//     {
//       "type": "line-horizontal"
//     },
//     {
//       "type": "line-info",
//       "name": "Die Summe der einzelnen Phasen kann aus technischen Gründen geringfügig von der Gesamtsumme abweichen."
//     }
//   ]
// }

// export const EMS30048_OWNER_AND_GUEST: OeFormlyView = {
//   "title": "Netz",
//   "lines": [
//     {
//       "type": "line",
//       "name": "Keine Netzverbindung!",
//       "channel": "_sum/GridMode"
//     },
//     {
//       "type": "line",
//       "name": "Bezug",
//       "channel": "meter0/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Einspeisung",
//       "channel": "meter0/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Phase 1 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL1",
//       "children": [
//         undefined,
//         undefined,
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL1"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 2 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL2",
//       "children": [
//         undefined,
//         undefined,
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL2"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 3 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL3",
//       "children": [
//         undefined,
//         undefined,
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL3"
//         }
//       ]
//     },
//     {
//       "type": "line-horizontal"
//     },
//     {
//       "type": "line-info",
//       "name": "Die Summe der einzelnen Phasen kann aus technischen Gründen geringfügig von der Gesamtsumme abweichen."
//     }
//   ]
// }

// export const EMS30048_ADMIN_AND_INSTALLER: OeFormlyView = {
//   "title": "Netz",
//   "lines": [
//     {
//       "type": "line",
//       "name": "Keine Netzverbindung!",
//       "channel": "_sum/GridMode"
//     },
//     {
//       "type": "line",
//       "name": "Bezug",
//       "channel": "meter0/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Einspeisung",
//       "channel": "meter0/ActivePower"
//     },
//     {
//       "type": "line",
//       "name": "Phase 1 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL1",
//       "children": [
//         {
//           "type": "line-item",
//           "channel": "meter0/VoltageL1"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/CurrentL1"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL1"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 2 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL2",
//       "children": [
//         {
//           "type": "line-item",
//           "channel": "meter0/VoltageL2"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/CurrentL2"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL2"
//         }
//       ]
//     },
//     {
//       "type": "line",
//       "name": "Phase 3 Bezug",
//       "indentation": TextIndentation.SINGLE,
//       "channel": "meter0/ActivePowerL3",
//       "children": [
//         {
//           "type": "line-item",
//           "channel": "meter0/VoltageL3"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/CurrentL3"
//         },
//         {
//           "type": "line-item",
//           "channel": "meter0/ActivePowerL3"
//         }
//       ]
//     },
//     {
//       "type": "line-horizontal"
//     },
//     {
//       "type": "line-info",
//       "name": "Die Summe der einzelnen Phasen kann aus technischen Gründen geringfügig von der Gesamtsumme abweichen."
//     }
//   ]
// };