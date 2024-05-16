// @ts-strict-ignore
import { DummyConfig } from "src/app/shared/edge/edgeconfig.spec";
import { OeTester } from "src/app/shared/genericComponents/shared/testing/common";

import { sharedSetup, TestContext } from "../../../../../shared/test/utils.spec";
import { DATA, LABELS } from "../../energy/chart/chart.constants.spec";
import { History } from "./channels.spec";
import { expectView } from "./chart.constants.spec";

describe('History Consumption', () => {
  const defaultEMS = DummyConfig.from(
    DummyConfig.Component.SOCOMEC_CONSUMPTION_METER("meter0", "Whirlpool"),
    DummyConfig.Component.EVCS_HARDY_BARTH("evcs0", "Charging Station"),
  );

  let TEST_CONTEXT: TestContext;
  beforeEach(async () =>
    TEST_CONTEXT = await sharedSetup(),
  );

  it('#getChartData()', () => {
    {

      // Line-Chart, phases
      expectView(defaultEMS, TEST_CONTEXT, 'line', History.DAY,
        {
          datasets: {
            data: [
              DATA('Gesamt: 93 kWh', [null, null, null, 0.5658045977011494, 0.5605148514851485, 0.561425925925926, 0.5732169811320755, 0.5658080808080809, 0.5879803921568627, 0.5842389380530973, 0.6074818181818182, 0.6050275229357799, 0.5956407766990292, 0.6025294117647059, 0.5781684210526317, 0.5816813186813187, 0.6495166666666666, 0.5602999999999999, 0.56703738317757, 0.6297672413793104, 0.5688613861386138, 0.5896039603960396, 0.6345090909090909, 0.6226633663366337, 0.6177196261682243, 0.608122641509434, 0.60278, 0.6508155339805825, 0.6058877551020408, 0.5943904761904762, 0.57636, 0.5650660377358491, 0.587495145631068, 0.626375, 0.6819428571428572, 0.6504629629629629, 0.6313738317757009, 0.68725, 0.5602452830188679, 0.5278952380952382, 0.6202222222222221, 0.5466355140186916, 0.5878130841121496, 0.604, 0.616295918367347, 0.5789405940594059, 0.5943396226415094, 0.6481078431372549, null, null, null, 0.5652183908045977, 0.5736481481481481, 0.5365462962962964, 0.558081081081081, 0.5747543859649124, 0.5743557692307693, 0.5969047619047619, 0.6540720720720721, 0.61521, 0.6128045977011494, 0.6361100000000001, 0.6137948717948718, 0.5388857142857142, 0.5371157894736842, 0.5511634615384615, 0.5588809523809524, 0.5591222222222223, 0.6185576923076923, 0.6543287671232877, 0.6160574712643678, 0.5889056603773585, 0.5787096774193549, 0.583036036036036, 0.572570093457944, 0.5952631578947368, 0.7450888888888888, 0.8207323943661972, 2.6511923076923076, 8.063131147540984, 6.952055555555556, 7.41341052631579, 6.695175, 6.688301075268817, 7.135544444444445, 6.9356125, 6.768398148148147, 6.962593220338983, 3.44951968503937, 0.6774427860696518, 0.9827305699481865, 0.8582012987012987, 0.7678924050632912, 0.761950495049505, 0.6821319796954315, 0.7254198473282443, 0.7021286549707602, 0.7309484536082475, 0.739, 0.730572864321608, 0.7547467532467532, 0.657373417721519, 0.6409480519480519, 0.6698156424581005, 0.7876280991735537, 2.931229357798165, 1.9542808988764044, 0.7775346534653466, 1.098538860103627, 1.2441524390243903, 2.9194913793103447, 2.9874188034188034, 3.4140294117647056, 1.3552, 2.7142824427480914, 2.6578703703703703, 2.8738923076923077, 6.140337078651685, 3.966560606060606, 3.0215789473684214, 1.7741875, 1.07646, 1.4268691588785047, 1.2610386740331492, 1.0193491124260354, 1.194960199004975, 1.2412878787878787, 3.323, 3.96845625, 1.4680961538461539, 3.6304166666666666, 2.2695301204819276, 2.554391025641026, 1.8457888888888887, 1.7289767441860464, 2.8620799999999997, 2.3241241379310345, 1.9640169491525423, 1.8084, 3.4660503597122303, 2.2974397590361444, 2.5300493827160495, 2.439358490566038, 2.0079060773480664, 1.7515, 1.4163181818181818, 1.4292298850574712, 1.4520298507462688, 1.4897204301075269, 1.6330952380952382, 1.8262928571428572, 1.6101904761904762, 1.680929292929293, 2.881743119266055, 3.5851634615384613, 3.6891666666666665, 3.6573402777777777, 3.6435348837209305, 3.7148645833333336, 3.731375, 3.74479, 3.6362363636363635, 4.273113924050633, 3.4461999999999997, 3.5387142857142857, 3.7906065573770493, 3.5276750000000003, 3.4676712328767123, 3.5595, 3.7982, 5.460666666666667, 1.3094406779661016, 1.5357454545454545, 3.4238260869565216, 3.3823636363636367, 3.4006315789473684, 2.95075, 3.386731707317073, 2.506, 1.4471666666666667, 1.4425999999999999, 0.946, 0.9425789473684211, 0.9507142857142856, 0.947, 0.9762857142857143, 1.7862857142857143, 1.5135777777777777, 1.4995625, 1.338, 1.3278125, 1.2739175257731958, 1.4387457627118645, 1.2484186046511627, 1.2866693548387098, 1.2848934911242604, 1.2237952755905512, 0.74809375, 0.8717684210526315, 0.8445338983050847, 0.7916749999999999, 0.8041932773109244, 0.7303737373737375, 0.7055024390243902, 0.6872407407407407, 0.6909939759036144, 0.751, 0.765139344262295, 0.686871794871795, 0.6697434210526315, 1.7678091603053436, 0.7246764705882353, 0.7482772277227723, 0.9401142857142858, 0.750368, 1.3660232558139536, 0.7274137931034482, 0.710719512195122, 0.6898555555555557, 0.739453488372093, 0.817875, 0.7304303797468354, 0.7355890410958904, 0.738225806451613, 1.906921739130435, 2.290785714285714, 1.2075072463768115, 1.1675890410958905, 1.2290208333333332, 1.1923777777777778, 1.2088717948717949, 1.367715909090909, 1.284223300970874, 1.1631739130434782, 1.15253, 1.1614545454545455, 1.2195681818181818, 1.183752808988764, 1.197778947368421, 1.2338888888888888, 1.275070588235294, 1.235554054054054, 1.20783908045977, 1.2416184210526318, 1.159042735042735, 1.1382948717948718, 1.1069915966386554, 1.1714504504504506, 1.223822429906542, 1.1221696428571428, 1.018892857142857, 0.9818285714285714, 0.9988363636363636, 6.056572916666667, 7.225528846153845, 7.193834782608696, 7.507321100917431, 7.588695238095238, 7.678689189189189, 9.469633027522935, 10.236186046511628, 10.106608695652174, 9.989125, 9.948229357798166, 9.796030927835051, 9.704217391304349, 8.454028571428571, 6.813476190476191, 6.706782608695652, 6.523452173913043, 6.661553571428572, 5.620777777777778, 0.8123142857142857, 0.8095892857142857, 0.8664786324786324, 0.8778319327731092, 0.8108141592920354, 0.8157121212121212, 0.7706470588235294, 0.7633157894736842, 0.7815151515151515, 0.8075833333333333, 0.824743119266055, 0.8762151898734176, 0.882424, 0.7502213114754098, 0.675954954954955, 0.6371222222222223]),
              DATA('Charging Station: 13,7 kWh', [null, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, null, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, null, 0, 0, 0, 0, 0, 0, null, 0, 4.104142857142857, 5.829969696969697, 5.806212121212121, 6.195333333333333, 5.777333333333333, 5.78625, 5.789727272727273, 5.789571428571429, 5.783052631578948, 5.772657142857144, 4.864842105263158, 0, 0, 0, 0, 0, 0, 0, 0, 0, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, null, null, null, 0, null, null, 0, 0, null, 0, 0, null, 0, 0, null, 0, 0, 0, 0, 0, 0, null, 0, null, null, 0, 0, null, null, 0, 0, 0, null, 0, null, null, 0, 0, null, 0, null, null, null, null, 0, null, 0, 0, null, null, null, null, null, 0, null, null, null, null, null, 0, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, 0, null, 0, null, 0, 0, 0, 0, 0, 0, null, null, 0, 0, null, 0, 0, 0, null, null, 0, 0, null, 0, 0, null, null, null, 0, null, null, null, null, null, null, 0, 0, 0, null, 0, 0, 0, 0, null, 0, null, 0, null, 0, 0, 0, null, 0, 0, 0, 0, 0, 5.213095238095239, 5.787580645161291, 5.7894848484848485, 5.790818181818182, 5.80734375, 5.820136363636364, 5.7988620689655175, 5.809041666666667, 5.8055625, 5.7943461538461545, 5.798, 5.740214285714285, 5.727242424242424, 5.742542857142857, 5.753424242424242, 5.7524, 5.731966666666667, 5.734323529411765, 5.335586206896552, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, null]),
              DATA('Whirlpool: 15,9 kWh', [0.12428571428571429, 0, null, 0, 0.17333333333333334, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, null, 0, 0, 0, 0, 0.11, 0.11333333333333333, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.145, 0, 0, 0, 0, 0, 0, null, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0.11333333333333333, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, null, 0, 0, null, 0.11333333333333333, 0, 0, null, 0, 0, 0, 0, 0, 0, 0, null, 0, 0, 0, 0, 0, 0, 0.13, 0, 0, 0, 0, 0, null, null, null, null, 0, 0, 0, 0, 0, null, 0, null, 0.14, null, null, null, 2.126875, 0.175, 0.176, null, 0.18, 0.18, 0.185, 0.18, null, 0.185, 0.19, 0.18, 0.18, 0.17625, 0.17583333333333334, 0.17, 0.175, 0.17, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null]),
              DATA('Sonstiger: 63,3 kWh', [null, null, null, 0.5658045977011494, 0.3871815181518151, 0.561425925925926, 0.5732169811320755, 0.5658080808080809, 0.5879803921568627, 0.5842389380530973, 0.6074818181818182, 0.6050275229357799, 0.5956407766990292, 0.6025294117647059, 0.5781684210526317, 0.5816813186813187, 0.6495166666666666, 0.5602999999999999, 0.56703738317757, 0.6297672413793104, 0.5688613861386138, 0.5896039603960396, 0.5245090909090909, 0.5093300330033004, 0.6177196261682243, 0.608122641509434, 0.60278, 0.6508155339805825, 0.6058877551020408, 0.5943904761904762, 0.57636, 0.5650660377358491, 0.587495145631068, 0.626375, 0.6819428571428572, 0.6504629629629629, 0.6313738317757009, 0.68725, 0.5602452830188679, 0.5278952380952382, 0.6202222222222221, 0.4016355140186916, 0.5878130841121496, 0.604, 0.616295918367347, 0.5789405940594059, 0.5943396226415094, 0.6481078431372549, null, null, null, 0.5652183908045977, 0.5736481481481481, 0.5365462962962964, 0.558081081081081, 0.5747543859649124, 0.5743557692307693, 0.5969047619047619, 0.6540720720720721, 0.5018766666666667, 0.6128045977011494, 0.6361100000000001, 0.6137948717948718, 0.5388857142857142, 0.5371157894736842, 0.5511634615384615, 0.5588809523809524, 0.5591222222222223, 0.6185576923076923, 0.6543287671232877, 0.6160574712643678, 0.5889056603773585, 0.5787096774193549, 0.583036036036036, 0.572570093457944, 0.5952631578947368, 0.7450888888888888, 0.7073990610328639, -1.452950549450549, 2.233161450571287, 1.1458434343434352, 1.2180771929824568, 0.9178416666666669, 0.9020510752688171, 1.3458171717171723, 1.1460410714285718, 0.9853455165691996, 1.189936077481839, -1.4153224202237875, 0.6774427860696518, 0.9827305699481865, 0.8582012987012987, 0.7678924050632912, 0.761950495049505, 0.6821319796954315, 0.5954198473282443, 0.7021286549707602, 0.7309484536082475, 0.739, 0.730572864321608, 0.7547467532467532, 0.657373417721519, 0.6409480519480519, 0.6698156424581005, 0.7876280991735537, 2.931229357798165, 1.9542808988764044, 0.7775346534653466, 1.098538860103627, 1.2441524390243903, 2.9194913793103447, 2.9874188034188034, 3.4140294117647056, 1.2151999999999998, 2.7142824427480914, 2.6578703703703703, 2.8738923076923077, 4.013462078651685, 3.791560606060606, 2.845578947368421, 1.7741875, 0.89646, 1.2468691588785048, 1.0760386740331491, 0.8393491124260355, 1.194960199004975, 1.0562878787878787, 3.133, 3.78845625, 1.288096153846154, 3.4541666666666666, 2.0936967871485943, 2.384391025641026, 1.6707888888888887, 1.5589767441860465, 2.8620799999999997, 2.3241241379310345, 1.9640169491525423, 1.8084, 3.4660503597122303, 2.2974397590361444, 2.5300493827160495, 2.439358490566038, 2.0079060773480664, 1.7515, 1.4163181818181818, 1.4292298850574712, 1.4520298507462688, 1.4897204301075269, 1.6330952380952382, 1.8262928571428572, 1.6101904761904762, 1.680929292929293, 2.881743119266055, 3.5851634615384613, 3.6891666666666665, 3.6573402777777777, 3.6435348837209305, 3.7148645833333336, 3.731375, 3.74479, 3.6362363636363635, 4.273113924050633, 3.4461999999999997, 3.5387142857142857, 3.7906065573770493, 3.5276750000000003, 3.4676712328767123, 3.5595, 3.7982, 5.460666666666667, 1.3094406779661016, 1.5357454545454545, 3.4238260869565216, 3.3823636363636367, 3.4006315789473684, 2.95075, 3.386731707317073, 2.506, 1.4471666666666667, 1.4425999999999999, 0.946, 0.9425789473684211, 0.9507142857142856, 0.947, 0.9762857142857143, 1.7862857142857143, 1.5135777777777777, 1.4995625, 1.338, 1.3278125, 1.2739175257731958, 1.4387457627118645, 1.2484186046511627, 1.2866693548387098, 1.2848934911242604, 1.2237952755905512, 0.74809375, 0.8717684210526315, 0.8445338983050847, 0.7916749999999999, 0.8041932773109244, 0.7303737373737375, 0.7055024390243902, 0.6872407407407407, 0.6909939759036144, 0.751, 0.765139344262295, 0.686871794871795, 0.6697434210526315, 1.7678091603053436, 0.7246764705882353, 0.7482772277227723, 0.9401142857142858, 0.750368, 1.3660232558139536, 0.7274137931034482, 0.710719512195122, 0.6898555555555557, 0.739453488372093, 0.817875, 0.7304303797468354, 0.7355890410958904, 0.738225806451613, 1.906921739130435, 2.290785714285714, 1.2075072463768115, 1.1675890410958905, 1.2290208333333332, 1.1923777777777778, 1.2088717948717949, 1.367715909090909, 1.284223300970874, 1.1631739130434782, 1.15253, 1.1614545454545455, 1.2195681818181818, 1.183752808988764, 1.197778947368421, 1.2338888888888888, 1.275070588235294, 1.235554054054054, 1.20783908045977, 1.2416184210526318, 1.159042735042735, 1.1382948717948718, 1.1069915966386554, 1.1714504504504506, 1.223822429906542, 1.1221696428571428, 1.018892857142857, 0.9818285714285714, 0.9988363636363636, 0.8434776785714284, 1.4379482009925546, 1.4043499341238475, 1.7165029190992493, 1.781351488095238, 1.8585528255528247, 3.6707709585574175, 4.427144379844961, 4.301046195652174, 4.194778846153845, 4.150229357798166, 4.055816642120766, 3.976974967061925, 2.711485714285714, 1.0600519480519486, 0.954382608695652, 0.7914855072463762, 0.9272300420168067, 0.28519157088122604, 0.8123142857142857, 0.8095892857142857, 0.8664786324786324, 0.8778319327731092, 0.8108141592920354, 0.8157121212121212, 0.7706470588235294, 0.7633157894736842, 0.7815151515151515, 0.8075833333333333, 0.824743119266055, 0.8762151898734176, 0.882424, 0.7502213114754098, 0.675954954954955, 0.6371222222222223]),
            ],
            labels: LABELS(History.DAY.dataChannelWithValues.result.timestamps),
            options: OeTester.ChartOptions.LINE_CHART_OPTIONS('hour', 'line', {},
            ),
          },
        });
    }

    {
      // Line-Chart, no phases, no phasecolors
      expectView(defaultEMS, TEST_CONTEXT, 'line', History.WEEK,
        {
          datasets: {
            data: [
              DATA('Gesamt: 354,1 kWh', [0.6639876181166504, 0.9375188284518828, 0.7615839462743844, 0.5997653758542141, 0.9896435035552682, 0.6662890016920473, 1.054177495462795, 1.0429070871481858, 0.6611515631183221, 0.7342340980187696, 0.8455174603174603, 0.9695830886670582, 0.5918473777253977, 1.9600339449541284, 0.9197469512195122, 1.2326632911392406, 0.5629648946840522, 0.7862627471383975, 0.9314229323308271, 0.837381753312946, 1.112301018483591, 0.9025108194395176, 0.8075094273743018, 0.988311229946524, 0.6140189035916823, 0.8686555377207063, 0.9069389035667107, 0.6733970826580227, 0.8276491228070175, 0.8519108776685869, 0.8529105374823197, 0.8674681647940076, 1.3030172261484099, 0.9506813929313929, 1.1247157794676808, 0.9087814790139906, 1.5545020215633425, 4.743944086021505, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null]),
              DATA('Charging Station: 157 kWh', [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4.586923076923077, 4.8462192982456145, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null]),
              DATA('Whirlpool: 100 kWh', [0.027956773446545735, 0.02793238434163701, 0.028003065134099617, 0.02795220588235294, 0.027998854961832062, 0.028116988416988415, 0.027987117552334944, 0.028404306220095696, 0.02210508757297748, 0.02458289572393098, 0.025521831735889244, 0.03394027565084227, 0.06640016920473774, 0.07485995085995086, 0.05630216802168022, 0.040562390158172236, 0.042727272727272725, 0.04185823488533704, 0.02956647864625302, 0.02693582887700535, 0.029227223427331885, 0.031081920903954802, 0.02950079575596817, 0.02929895178197065, 0.02774064837905237, 0.027340638930163447, 0.027272103350827612, 0.027199391171993912, 0.02697577592732778, 0.02688116057233704, 0.02704352806414662, 0.026709154929577466, 0.024765209940017137, 0.020726594301221167, 0.024283930058284765, 0.027086363636363638, 0.04770666666666666, 0.05307067510548523, 0.05273466476462197, 0.054757768924302785, 0.05023135033050047, 0.041280141843971635, 0.03150934119960669, 0.026471978392977717, 0.031050412465627866, 0.029384710234278667, 0.02795327604726101, 0.028357904496986556, 0.025564085881587508, null, 0.026944094488188977, 0.027036082474226807, 0.026367164179104476, 0.025185562632696393, 0.026994444444444442, 0.025911782850092536, 0.01343197071384991, 0.024309846431797653, 0.02523471223021583, 0.026126705653021443, 0.06064339781328848, 0.03996433289299867, 0.037667578659370725, 0.050665427509293676, 0.04189218523878437, 0.02988109495295124, 0.02693968253968254, 0.025741935483870968, 0.028640000000000002, 0.031331460674157305, 0.030017262638717634, 0.028636402753872633, 0.028408956692913385, 0.02814567233384853, 0.02825509028044564, 0.028126129666011787, 0.028175675675675676, 0.027409126063418405, 0.027549439844130538, 0.027246363209760677, 0.02296663244353183, 0.02165732959850607, 0.022306306306306308, 0.025892307692307694, 0.0518, 0.05290322580645162, 0.05902597402597402, 0.03935110294117647, 0.03387375113533152, 0.02998160073597056, 0.0264819734345351, 0.029473684210526315, 0.028432854465958775, 0.029162214983713356, 0.028663861386138613, 0.02823558082859464, 0.028460033305578683, 0.029076231430805316, 0.028803389830508477, 0.02862062404870624, 0.028578023655093474, 0.02837057010785824, 0.02746546431312356, 0.027873293515358364, 0.025733505154639177, 0.02635780287474333, 0.027692853246044733, 0.024205128205128205, null, 0.026746031746031747, 0.023178571428571427, 0.026168618266978923, 0.028333646616541353, 0.026758232235701906, 0.023993934142114384, 0.02471902131018153, 0.02823051948051948, 0.028745721271393642, 0.028485825458588104, 0.02822232263895165, 0.028392755392755393, 0.028338354151359293, 0.027164207340143777, 0.02694932432432432, 0.026942610652663165, 0.027809220389805097, 0.027980593607305936, 0.02826596758817922, 0.029656794425087108, 0.025145056246299584, 0.02520321410057024, 0.02882728749323227, 0.03222576966932725, 0.031200444197667963, 0.030594259988745078, 0.04334990059642147, 0.04615876288659794, 0.03340802469135802, 0.027489320388349517, 0.025818808777429467, 0.02795967741935484, 0.028969951391957578, 0.027549955791335103, 0.02834137055837563, 0.028250760538896132, 0.027812838360402166, 0.02810333080999243, 0.027840314136125657, 0.027800384615384616, 0.027979583975346688, 0.027235769230769234, 0.026924960505529224, 0.029038479809976248, 0.02793128390596745, 0.03410349373764008, 0.0327387339055794, 0.029509929906542054, 0.03700855745721272, 0.03224008574490889, 0.02770136363636364, 0.030787037037037036, 0.025786028602860285, 0.029997148966500357, 0.03289406099518459, 0.03532872928176795, 0.025655647382920112, 0.02746238030095759, 0.02827226647356988]),
              DATA('Sonstiger: 97,1 kWh', [0.6360308446701046, 0.9095864441102458, 0.7335808811402847, 0.5718131699718612, 0.9616446485934361, 0.6381720132750589, 1.02619037791046, 1.0145027809280902, 0.6390464755453447, 0.7096512022948386, 0.819995628581571, 0.935642813016216, 0.5254472085206601, 1.8851739940941776, 0.863444783197832, 1.1921009009810684, 0.5202376219567795, 0.7444045122530605, 0.901856453684574, 0.8104459244359407, 1.0830737950562592, 0.8714288985355628, 0.7780086316183336, 0.9590122781645534, 0.58627825521263, 0.8413148987905428, 0.8796668002158831, 0.6461976914860288, 0.8006733468796897, 0.8250297170962498, 0.825867009418173, 0.8407590098644301, 1.2782520162083928, 0.9299547986301717, 1.100431849409396, 0.881695115377627, -3.0801277220264014, 0, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null]),
            ],
            labels: LABELS(History.WEEK.dataChannelWithValues.result.timestamps),
            options: OeTester.ChartOptions.LINE_CHART_OPTIONS('day', 'line', {}),
          },
        });
    }

    {
      // Bar-Chart, no phases, no phasecolors
      expectView(defaultEMS, TEST_CONTEXT, 'bar', History.MONTH,
        {
          datasets: {
            data: [
              DATA('Gesamt: 1.033,4 kWh', [1.7843478512581188, 0.9551978135997077, 1.6049176251387696, 1.8215474663613151, 1.2040116271893009, 1.037408900359932, 1.287891020875591, 1.1830577354221141, 1.0278784377276404, 1.4127831967543791, 0.7372379614126091, 0.7306143394985477, 1.7274568452231196, 2.04548274638676, 1.289986737393844, 1.8665731598778827, 0.896448259060122, 1.2710485678635468, 1.4897323302960577, 1.3679696489029907, 2.3756092366846033, 2.3687291028362236, 2.68660326649514, 1.5970390753045414, 0.9726650191463931, 1.7742336901685731, 2.953838257099637, 0.9173087792235759, 1.1729800154902883, null, null]),
              DATA('Charging Station: 328,5 kWh', [0.598058461158158, 0, 0.6073861225965935, 0.8913491768679577, 0.2696236843407865, 0, 0.30641009901340227, 0.2205140720984315, 0.05015525733301707, 0.35642246970726826, 0, 0, 0.8800942036863182, 0.9774033026217929, 0.21023831546417276, 0.7870712558876392, 0, 0.16550743075023164, 0.5132605942604259, 0.14906825174512017, 0.8836100586172083, 1.2031171015303659, 1.4672314708234808, 0.5536230686820822, 0, 0.7148486366912177, 1.8367765179313804, 0, 0.300374916784946, null, null]),
              DATA('Whirlpool: 21,6 kWh', [0.01553700680772126, 0.01397856617670663, 0.01622420643945345, 0.01542515500397099, 0.01641557086346929, 0.015280936198647838, 0.015028196655704793, 0.015146427851947055, 0.015567356482244768, 0.017404197969735604, 0.017635152684968115, 0.014140394156739467, 0.016107488806188935, 0.01675001277671301, 0.015512752420609467, 0.014951999780788457, 0.013733457057782297, 0.016868671206682027, 0.014770978996449592, 0.01647697124898351, 0.016772182319685667, 0.016288562161254704, 0.015417362341926746, 0.015892028990939402, 0.013811584043067414, 0.01481782772305683, 0.014165639305307825, 0.016223347257543284, 0.014495672387672808, null, null]),
              DATA('Sonstiger: 683,3 kWh', [1.1707523832922395, 0.941219247423001, 0.9813072961027226, 0.9147731344893865, 0.9179723719850451, 1.0221279641612842, 0.966452725206484, 0.9473972354717355, 0.9621558239123786, 1.0389565290773752, 0.719602808727641, 0.7164739453418082, 0.8312551527306125, 1.0513294309882544, 1.0642356695090618, 1.064549904209455, 0.8827148020023398, 1.0886724659066331, 0.9617007570391821, 1.2024244259088868, 1.4752269957477093, 1.149323439144603, 1.2039544333297325, 1.0275239776315197, 0.9588534351033258, 1.0445672257542986, 1.1028960998629487, 0.9010854319660326, 0.8581094263176695, null, null]),
            ],
            labels: LABELS(History.MONTH.energyPerPeriodChannelWithValues.result.timestamps),
            options: OeTester.ChartOptions.BAR_CHART_OPTIONS('day', 'bar', {}),
          },
        });
    }

    {
      // Bar-Chart, phases
      expectView(defaultEMS, TEST_CONTEXT, 'bar', History.YEAR,
        {
          datasets: {
            data: [
              DATA('Gesamt: 14.863,7 kWh', [1682.941, 1834.729, 1805.222, 1934.144, 1757.577, 1511.352, 2306.988, 2029.933, 0, 0, 0, 0]),
              DATA('Charging Station: 2.071,1 kWh', [69.104, 131.703, 25.773, 51.085, 169.943, 332.522, 748.189, 540.74, 0, 0, 0, 0]),
              DATA('Whirlpool: 1.908,7 kWh', [338.07, 312.38, 298.93, 317.7, 200.21, 151.16, 145.88, 144.28, 0, 0, 0, 0]),
              DATA('Sonstiger: 10.883,9 kWh', [1275.767, 1390.6460000000002, 1480.519, 1565.359, 1387.424, 1027.67, 1412.9189999999999, 1344.913, 0, 0, 0, 0]),
            ],
            labels: LABELS(History.YEAR.energyPerPeriodChannelWithValues.result.timestamps),
            options: OeTester.ChartOptions.BAR_CHART_OPTIONS('month', 'bar', {}),
          },
        });
    }
  });
});
