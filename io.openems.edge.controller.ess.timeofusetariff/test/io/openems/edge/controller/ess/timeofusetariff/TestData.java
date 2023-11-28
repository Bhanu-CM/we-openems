package io.openems.edge.controller.ess.timeofusetariff;

public class TestData {

	// Edge 888; 06.11.2023

	public static final Integer[] PRODUCTION_888_20231106 = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 12, 19, 24, 92, 301, 441, 653, 741, 1921, 1923, 1649, 2045, 2638, 3399, 4071, 4359,
			4516, 5541, 6993, 6292, 3902, 7700, 9098, 9555, 8119, 6868, 6560, 6380, 6193, 5389, 4349, 3743, 5367, 5319,
			4383, 2243, 1122, 1315, 1107, 268, 48, 2, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0 };

	public static final Integer[] CONSUMPTION_888_20231106 = { 308, 285, 384, 471, 480, 385, 464, 448, 288, 333, 346,
			313, 1786, 332, 300, 259, 373, 358, 279, 308, 309, 415, 392, 299, 2913, 3105, 4416, 4442, 497, 5910, 4106,
			2171, 3898, 922, 1601, 1088, 303, 2384, 430, 2428, 2899, 371, 613, 1663, 366, 2072, 456, 1589, 2004, 488,
			199, 1628, 613, 198, 1796, 202, 1180, 4975, 4493, 5511, 7757, 2926, 2640, 4335, 2630, 2799, 5111, 2979,
			3062, 4842, 4194, 4474, 4750, 4876, 1238, 1395, 1425, 1123, 3366, 4088, 418, 436, 3234, 1504, 1092, 1853,
			365, 628, 2095, 552, 1113, 1808, 3223, 1629, 1329, 264 };

	public static final Float[] PRICES_888_20231106 = { 155F, 152F, 152F, 152F, 157F, 172F, 238F, 266F, 266F, 241F,
			224F, 219F, 221F, 232F, 248F, 271F, 286F, 316F, 332F, 318F, 284F, 278F, 270F, 257F };

	// Edge 12786; 21.11.2023

	public static final Integer[] PRODUCTION_12786_20231121 = { 339, 375, 250, 242, 181, 48, -1, -2, -1, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 11, 65, 51, 51, 103, 334, 306, 234, 338, 337, 426,
			518, 517, 269, 708, 741, 342, 520, 505, 523, 502, 538, 471, 448, 367, 0, 0 };

	public static final Integer[] CONSUMPTION_12786_20231121 = { 385, 192, 309, 2184, 2079, 1780, 1832, 1893, 1563,
			1363, 1935, 1125, 946, 867, 884, 828, 863, 843, 856, 961, 897, 859, 882, 864, 973, 989, 1003, 1012, 997,
			981, 968, 983, 911, 828, 739, 184, 172, 240, 394, 44, 43, 36, 88, 199, 159, 39, 157, 118, 97, 254, 109, 52,
			45, 180, 286, 190, 138, 87, 174, 312, 78, 55, 227, 502, 286, 265, 341, 270, 273, 273, 280, 265, 248, 228,
			177, 166, 296, 331, 260, 260, 301, 354, 364, 389, 282, 349, 338, 253, 248, 345, 444, 329, 253, 341, 332,
			66 };

	public static final Float[] PRICES_12786_20231121 = { 138.37F, 138.37F, 140.34F, 140.34F, 140.34F, 140.34F, 143.92F,
			143.92F, 143.92F, 143.92F, 146.97F, 146.97F, 146.97F, 146.97F, 142.08F, 142.08F, 142.08F, 142.08F, 132.35F,
			132.35F, 132.35F, 132.35F, 124.79F, 124.79F, 124.79F, 124.79F, 110.52F, 110.52F, 110.52F, 110.52F, 110.91F,
			110.91F, 110.91F, 110.91F, 98.04F, 98.04F, 98.04F, 98.04F, 98F, 98F, 98F, 98F, 95.01F, 95.01F, 95.01F,
			95.01F, 95.01F, 95.01F, 95.01F, 95.01F, 92F, 92F, 92F, 92F, 92.49F, 92.49F, 92.49F, 92.49F, 94.87F, 94.87F,
			94.87F, 94.87F, 120.17F, 120.17F, 120.17F, 120.17F, 140.73F, 140.73F, 140.73F, 140.73F, 151.64F, 151.64F,
			151.64F, 151.64F, 131.33F, 131.33F, 131.33F, 131.33F, 115.03F, 115.03F, 115.03F, 115.03F, 103.63F, 103.63F,
			103.63F, 103.63F, 95.69F, 95.69F, 95.69F, 95.69F, 102.44F, 102.44F, 102.44F, 102.44F, 257F, 257F };

	//

	public static final Integer[] PRODUCTION_PREDICTION_QUARTERLY = {
			/* 00:00-03:45 */
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //
			/* 04:00-07:45 */
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 74, 297, 610, //
			/* 08:00-11:45 */
			913, 1399, 1838, 2261, 2662, 3052, 3405, 3708, 4011, 4270, 4458, 4630, 4794, 4908, 4963, 4960, //
			/* 12:00-15:45 */
			4973, 4940, 4859, 4807, 4698, 4530, 4348, 4147, 1296, 1399, 1838, 1261, 1662, 1052, 1405, 1402,
			/* 16:00-19:45 */
			1662, 1052, 1405, 1630, 1285, 1520, 1250, 910, 0, 0, 0, 0, 0, 0, 0, 0, //
			/* 20:00-23:45 */
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //
			/* 00:00-03:45 */
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //
			/* 04:00-07:45 */
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 130, 402, 667, //
			/* 08:00-11:45 */
			1023, 1631, 2020, 2420, 2834, 3237, 3638, 4006, 4338, 4597, 4825, 4965, 5111, 5213, 5268, 5317, //
			/* 12:00-15:45 */
			5321, 5271, 5232, 5193, 5044, 4915, 4738, 4499, 3702, 3226, 3046, 2857, 2649, 2421, 2184, 1933, //
			/* 16:00-19:45 */
			1674, 1364, 1070, 754, 447, 193, 52, 0, 0, 0, 0, 0, 0, 0, 0, 0, //
			/* 20:00-23:45 */
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 //
	};

	public static final Integer[] PRODUCTION_PREDICTION_HOURLY = {
			/* 00:00-12:00 */
			0, 0, 0, 0, 0, 0, 0, 74, 297, 610, 913, 1399,
			/* 13:00-24:00 */
			1838, 2261, 2662, 3052, 1520, 1250, 910, 0, 0, 0, 0, 0, //
	};

	public static final Integer[] CONSUMPTION_PREDICTION_HOURLY = {
			/* 00:00-12:00 */
			1021, 1208, 713, 931, 2847, 2551, 1558, 1234, 433, 633, 1355, 606, //
			/* 13:00-24:00 */
			430, 1432, 1121, 502, 294, 1048, 1194, 914, 1534, 1226, 1235, 977, //
	};

	public static final Integer[] CONSUMPTION_PREDICTION_QUARTERLY = {

			/* 00:00-03:450 */
			1021, 1208, 713, 931, 2847, 2551, 1558, 1234, 433, 633, 1355, 606, 430, 1432, 1121, 502, //
			/* 04:00-07:45 */
			294, 1048, 1194, 914, 1534, 1226, 1235, 977, 578, 1253, 1983, 1417, 513, 929, 1102, 445, //
			/* 08:00-11:45 */
			1208, 2791, 2729, 2609, 2086, 1454, 848, 816, 2610, 3150, 2036, 1180, 359, 1316, 3447, 2104, //
			/* 12:00-15:45 */
			905, 802, 828, 812, 863, 633, 293, 379, 1250, 2296, 2436, 2140, 2135, 1196, 2230, 1725,
			/* 16:00-19:45 */
			2365, 1758, 2325, 2264, 2181, 2167, 2228, 1082, 777, 417, 798, 1268, 409, 830, 1191, 417, //
			/* 20:00-23:45 */
			1087, 2958, 2946, 2235, 1343, 483, 796, 1201, 567, 395, 989, 1066, 370, 989, 1255, 660, //
			/* 00:00-03:45 */
			349, 880, 1186, 580, 327, 911, 1135, 553, 265, 938, 1165, 567, 278, 863, 1239, 658, //
			/* 04:00-07:45 */
			236, 816, 1173, 1131, 498, 550, 1344, 1226, 874, 504, 1733, 1809, 1576, 369, 771, 2583, //
			/* 08:00-11:45 */
			3202, 2174, 1878, 2132, 2109, 1895, 1565, 1477, 1613, 1716, 1867, 1726, 1700, 1787, 1755, 1734, //
			/* 12:00-15:45 */
			1380, 691, 338, 168, 199, 448, 662, 205, 183, 70, 169, 276, 149, 76, 195, 168, //
			/* 16:00-19:45 */
			159, 266, 135, 120, 224, 979, 2965, 1337, 1116, 795, 334, 390, 433, 369, 762, 2908, //
			/* 20:00-23:45 */
			3226, 2358, 1778, 1002, 455, 654, 534, 1587, 1638, 459, 330, 258, 368, 728, 1096, 878 //
	};

	public static final Float[] HOURLY_PRICES = { //
			158.95f, 160.98f, 171.95f, 174.96f, //
			161.93f, 152f, 120.01f, 111.03f, //
			105.04f, 105f, 74.23f, 73.28f, //
			67.97f, 72.53f, 89.66f, 150.01f, //
			173.54f, 178.4f, 158.91f, 140.01f, //
			149.99f, 157.43f, 130.9f, 120.14f //
	};

	public static final Float[] HOURLY_PRICES_SUMMER = { //
			70.95f, 71.98f, 71.95f, 74.96f, //
			78.93f, 80f, 84.01f, 111.03f, //
			105.04f, 105f, 74.23f, 73.28f, //
			67.97f, 72.53f, 89.66f, 150.01f, //
			173.54f, 178.4f, 158.91f, 140.01f, //
			149.99f, 157.43f, 130.9f, 120.14f //
	};

	public static final Float[] PAST_HOURLY_PRICES = { //
			158.95f, 160.98f, 171.95f, 174.96f, //
			161.93f, 152f, 120.01f, 111.03f, //
			105.04f, 105f, 74.23f, 73.28f, //
	};

	public static final Integer[] STATES = { //
			1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 2, //
			2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, //
			2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, //
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, //
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, //
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, //
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, //
			2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 //
	};

	public static final Integer[] PAST_STATES = { //
			1, 1, 1, 1, //
			1, 3, 3, 1, //
			2, 1, 2, 2, //
	};
}
