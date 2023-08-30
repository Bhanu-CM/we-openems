import { DummyConfig } from "src/app/shared/edge/edgeconfig.spec";
import { EdgeConfig } from "src/app/shared/shared";
import { removeFunctions, TestContext } from "src/app/shared/test/utils.spec";

import { OeChartTester } from "../../../../../shared/genericComponents/shared/tester";
import { ChartComponent } from "./chart";

export function expectView(config: EdgeConfig, testContext: TestContext, chartType: 'line' | 'bar', channels: DummyConfig.OeChannels, view: OeChartTester.View): void {

  expect(removeFunctions(OeChartTester
    .apply(ChartComponent
      .getChartData(
        DummyConfig.convertDummyEdgeConfigToRealEdgeConfig(config),
        testContext.translate), chartType, channels, testContext)))
    .toEqual(removeFunctions(view));
};