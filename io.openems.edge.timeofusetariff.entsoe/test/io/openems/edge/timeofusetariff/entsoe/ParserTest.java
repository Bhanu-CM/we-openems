package io.openems.edge.timeofusetariff.entsoe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import io.openems.edge.common.currency.Currency;

public class ParserTest {

	private static String XML = """
			<?xml version="1.0" encoding="UTF-8"?>
			<Publication_MarketDocument xmlns="urn:iec62325.351:tc57wg16:451-3:publicationdocument:7:0">
				<mRID>946edcf0cf33426aa666e1069ececbe7</mRID>
				<revisionNumber>1</revisionNumber>
				<type>A44</type>
				<sender_MarketParticipant.mRID codingScheme="A01">10X1001A1001A450</sender_MarketParticipant.mRID>
				<sender_MarketParticipant.marketRole.type>A32</sender_MarketParticipant.marketRole.type>
				<receiver_MarketParticipant.mRID codingScheme="A01">10X1001A1001A450</receiver_MarketParticipant.mRID>
				<receiver_MarketParticipant.marketRole.type>A33</receiver_MarketParticipant.marketRole.type>
				<createdDateTime>2023-06-01T12:40:44Z</createdDateTime>
				<period.timeInterval>
					<start>2023-05-31T22:00Z</start>
					<end>2023-06-01T22:00Z</end>
				</period.timeInterval>
				<TimeSeries>
					<mRID>1</mRID>
					<businessType>A62</businessType>
					<in_Domain.mRID codingScheme="A01">10Y1001A1001A82H</in_Domain.mRID>
					<out_Domain.mRID codingScheme="A01">10Y1001A1001A82H</out_Domain.mRID>
					<currency_Unit.name>EUR</currency_Unit.name>
					<price_Measure_Unit.name>MWH</price_Measure_Unit.name>
					<curveType>A01</curveType>
						<Period>
							<timeInterval>
								<start>2023-05-31T22:00Z</start>
								<end>2023-06-01T22:00Z</end>
							</timeInterval>
							<resolution>PT15M</resolution>
								<Point>
									<position>1</position>
									<price.amount>109.93</price.amount>
								</Point>
								<Point>
									<position>2</position>
									<price.amount>85.84</price.amount>
								</Point>
								<Point>
									<position>3</position>
									<price.amount>65.09</price.amount>
								</Point>
								<Point>
									<position>4</position>
									<price.amount>55.07</price.amount>
								</Point>
								<Point>
									<position>5</position>
									<price.amount>90.10</price.amount>
								</Point>
								<Point>
									<position>6</position>
									<price.amount>78.30</price.amount>
								</Point>
								<Point>
									<position>7</position>
									<price.amount>71.20</price.amount>
								</Point>
								<Point>
									<position>8</position>
									<price.amount>60.80</price.amount>
								</Point>
								<Point>
									<position>9</position>
									<price.amount>79.70</price.amount>
								</Point>
								<Point>
									<position>10</position>
									<price.amount>70.60</price.amount>
								</Point>
								<Point>
									<position>11</position>
									<price.amount>75.10</price.amount>
								</Point>
								<Point>
									<position>12</position>
									<price.amount>66.14</price.amount>
								</Point>
								<Point>
									<position>13</position>
									<price.amount>74.00</price.amount>
								</Point>
								<Point>
									<position>14</position>
									<price.amount>70.60</price.amount>
								</Point>
								<Point>
									<position>15</position>
									<price.amount>70.20</price.amount>
								</Point>
								<Point>
									<position>16</position>
									<price.amount>71.34</price.amount>
								</Point>
								<Point>
									<position>17</position>
									<price.amount>52.70</price.amount>
								</Point>
								<Point>
									<position>18</position>
									<price.amount>62.10</price.amount>
								</Point>
								<Point>
									<position>19</position>
									<price.amount>77.40</price.amount>
								</Point>
								<Point>
									<position>20</position>
									<price.amount>93.40</price.amount>
								</Point>
								<Point>
									<position>21</position>
									<price.amount>40.60</price.amount>
								</Point>
								<Point>
									<position>22</position>
									<price.amount>56.20</price.amount>
								</Point>
								<Point>
									<position>23</position>
									<price.amount>87.20</price.amount>
								</Point>
								<Point>
									<position>24</position>
									<price.amount>144.06</price.amount>
								</Point>
								<Point>
									<position>25</position>
									<price.amount>50.68</price.amount>
								</Point>
								<Point>
									<position>26</position>
									<price.amount>96.20</price.amount>
								</Point>
								<Point>
									<position>27</position>
									<price.amount>117.60</price.amount>
								</Point>
								<Point>
									<position>28</position>
									<price.amount>121.50</price.amount>
								</Point>
								<Point>
									<position>29</position>
									<price.amount>125.10</price.amount>
								</Point>
								<Point>
									<position>30</position>
									<price.amount>111.80</price.amount>
								</Point>
								<Point>
									<position>31</position>
									<price.amount>91.80</price.amount>
								</Point>
								<Point>
									<position>32</position>
									<price.amount>77.46</price.amount>
								</Point>
								<Point>
									<position>33</position>
									<price.amount>179.06</price.amount>
								</Point>
								<Point>
									<position>34</position>
									<price.amount>111.90</price.amount>
								</Point>
								<Point>
									<position>35</position>
									<price.amount>69.50</price.amount>
								</Point>
								<Point>
									<position>36</position>
									<price.amount>30.10</price.amount>
								</Point>
								<Point>
									<position>37</position>
									<price.amount>151.70</price.amount>
								</Point>
								<Point>
									<position>38</position>
									<price.amount>96.20</price.amount>
								</Point>
								<Point>
									<position>39</position>
									<price.amount>69.91</price.amount>
								</Point>
								<Point>
									<position>40</position>
									<price.amount>7.20</price.amount>
								</Point>
								<Point>
									<position>41</position>
									<price.amount>114.91</price.amount>
								</Point>
								<Point>
									<position>42</position>
									<price.amount>75.20</price.amount>
								</Point>
								<Point>
									<position>43</position>
									<price.amount>49.90</price.amount>
								</Point>
								<Point>
									<position>44</position>
									<price.amount>1.20</price.amount>
								</Point>
								<Point>
									<position>45</position>
									<price.amount>89.91</price.amount>
								</Point>
								<Point>
									<position>46</position>
									<price.amount>64.90</price.amount>
								</Point>
								<Point>
									<position>47</position>
									<price.amount>29.20</price.amount>
								</Point>
								<Point>
									<position>48</position>
									<price.amount>-11.47</price.amount>
								</Point>
								<Point>
									<position>49</position>
									<price.amount>69.80</price.amount>
								</Point>
								<Point>
									<position>50</position>
									<price.amount>34.90</price.amount>
								</Point>
								<Point>
									<position>51</position>
									<price.amount>6.10</price.amount>
								</Point>
								<Point>
									<position>52</position>
									<price.amount>-20.00</price.amount>
								</Point>
								<Point>
									<position>53</position>
									<price.amount>44.20</price.amount>
								</Point>
								<Point>
									<position>54</position>
									<price.amount>24.91</price.amount>
								</Point>
								<Point>
									<position>55</position>
									<price.amount>-12.03</price.amount>
								</Point>
								<Point>
									<position>56</position>
									<price.amount>-19.94</price.amount>
								</Point>
								<Point>
									<position>57</position>
									<price.amount>-25.30</price.amount>
								</Point>
								<Point>
									<position>58</position>
									<price.amount>-13.50</price.amount>
								</Point>
								<Point>
									<position>59</position>
									<price.amount>18.83</price.amount>
								</Point>
								<Point>
									<position>60</position>
									<price.amount>39.90</price.amount>
								</Point>
								<Point>
									<position>61</position>
									<price.amount>-49.90</price.amount>
								</Point>
								<Point>
									<position>62</position>
									<price.amount>-10.80</price.amount>
								</Point>
								<Point>
									<position>63</position>
									<price.amount>21.40</price.amount>
								</Point>
								<Point>
									<position>64</position>
									<price.amount>61.62</price.amount>
								</Point>
								<Point>
									<position>65</position>
									<price.amount>-61.69</price.amount>
								</Point>
								<Point>
									<position>66</position>
									<price.amount>7.16</price.amount>
								</Point>
								<Point>
									<position>67</position>
									<price.amount>58.60</price.amount>
								</Point>
								<Point>
									<position>68</position>
									<price.amount>109.92</price.amount>
								</Point>
								<Point>
									<position>69</position>
									<price.amount>-35.10</price.amount>
								</Point>
								<Point>
									<position>70</position>
									<price.amount>39.93</price.amount>
								</Point>
								<Point>
									<position>71</position>
									<price.amount>109.93</price.amount>
								</Point>
								<Point>
									<position>72</position>
									<price.amount>139.94</price.amount>
								</Point>
								<Point>
									<position>73</position>
									<price.amount>29.91</price.amount>
								</Point>
								<Point>
									<position>74</position>
									<price.amount>54.10</price.amount>
								</Point>
								<Point>
									<position>75</position>
									<price.amount>105.11</price.amount>
								</Point>
								<Point>
									<position>76</position>
									<price.amount>149.91</price.amount>
								</Point>
								<Point>
									<position>77</position>
									<price.amount>75.05</price.amount>
								</Point>
								<Point>
									<position>78</position>
									<price.amount>93.33</price.amount>
								</Point>
								<Point>
									<position>79</position>
									<price.amount>130.10</price.amount>
								</Point>
								<Point>
									<position>80</position>
									<price.amount>139.96</price.amount>
								</Point>
								<Point>
									<position>81</position>
									<price.amount>139.92</price.amount>
								</Point>
								<Point>
									<position>82</position>
									<price.amount>110.22</price.amount>
								</Point>
								<Point>
									<position>83</position>
									<price.amount>95.08</price.amount>
								</Point>
								<Point>
									<position>84</position>
									<price.amount>95.05</price.amount>
								</Point>
								<Point>
									<position>85</position>
									<price.amount>139.98</price.amount>
								</Point>
								<Point>
									<position>86</position>
									<price.amount>114.80</price.amount>
								</Point>
								<Point>
									<position>87</position>
									<price.amount>90.09</price.amount>
								</Point>
								<Point>
									<position>88</position>
									<price.amount>80.07</price.amount>
								</Point>
								<Point>
									<position>89</position>
									<price.amount>134.70</price.amount>
								</Point>
								<Point>
									<position>90</position>
									<price.amount>100.68</price.amount>
								</Point>
								<Point>
									<position>91</position>
									<price.amount>80.03</price.amount>
								</Point>
								<Point>
									<position>92</position>
									<price.amount>70.02</price.amount>
								</Point>
								<Point>
									<position>93</position>
									<price.amount>119.70</price.amount>
								</Point>
								<Point>
									<position>94</position>
									<price.amount>82.41</price.amount>
								</Point>
								<Point>
									<position>95</position>
									<price.amount>75.10</price.amount>
								</Point>
								<Point>
									<position>96</position>
									<price.amount>65.07</price.amount>
								</Point>
						</Period>
				</TimeSeries>
				<TimeSeries>
					<mRID>2</mRID>
					<businessType>A62</businessType>
					<in_Domain.mRID codingScheme="A01">10Y1001A1001A82H</in_Domain.mRID>
					<out_Domain.mRID codingScheme="A01">10Y1001A1001A82H</out_Domain.mRID>
					<currency_Unit.name>EUR</currency_Unit.name>
					<price_Measure_Unit.name>MWH</price_Measure_Unit.name>
					<curveType>A01</curveType>
						<Period>
							<timeInterval>
								<start>2023-05-31T22:00Z</start>
								<end>2023-06-01T22:00Z</end>
							</timeInterval>
							<resolution>PT60M</resolution>
								<Point>
									<position>1</position>
									<price.amount>84.15</price.amount>
								</Point>
								<Point>
									<position>2</position>
									<price.amount>74.30</price.amount>
								</Point>
								<Point>
									<position>3</position>
									<price.amount>70.10</price.amount>
								</Point>
								<Point>
									<position>4</position>
									<price.amount>66.72</price.amount>
								</Point>
								<Point>
									<position>5</position>
									<price.amount>67.70</price.amount>
								</Point>
								<Point>
									<position>6</position>
									<price.amount>80.45</price.amount>
								</Point>
								<Point>
									<position>7</position>
									<price.amount>97.04</price.amount>
								</Point>
								<Point>
									<position>8</position>
									<price.amount>108.23</price.amount>
								</Point>
								<Point>
									<position>9</position>
									<price.amount>99.07</price.amount>
								</Point>
								<Point>
									<position>10</position>
									<price.amount>87.30</price.amount>
								</Point>
								<Point>
									<position>11</position>
									<price.amount>68.19</price.amount>
								</Point>
								<Point>
									<position>12</position>
									<price.amount>59.92</price.amount>
								</Point>
								<Point>
									<position>13</position>
									<price.amount>38.86</price.amount>
								</Point>
								<Point>
									<position>14</position>
									<price.amount>9.35</price.amount>
								</Point>
								<Point>
									<position>15</position>
									<price.amount>3.01</price.amount>
								</Point>
								<Point>
									<position>16</position>
									<price.amount>13.35</price.amount>
								</Point>
								<Point>
									<position>17</position>
									<price.amount>56.14</price.amount>
								</Point>
								<Point>
									<position>18</position>
									<price.amount>72.00</price.amount>
								</Point>
								<Point>
									<position>19</position>
									<price.amount>87.86</price.amount>
								</Point>
								<Point>
									<position>20</position>
									<price.amount>100.46</price.amount>
								</Point>
								<Point>
									<position>21</position>
									<price.amount>120.04</price.amount>
								</Point>
								<Point>
									<position>22</position>
									<price.amount>103.43</price.amount>
								</Point>
								<Point>
									<position>23</position>
									<price.amount>95.41</price.amount>
								</Point>
								<Point>
									<position>24</position>
									<price.amount>86.53</price.amount>
								</Point>
						</Period>
				</TimeSeries>
			</Publication_MarketDocument>
						""";

	@Test
	public void testParse() throws IOException, ParserConfigurationException, SAXException {
		var currencyExchangeValue = 1.0;
		var result = Utils.parsePrices(XML, "PT15M", currencyExchangeValue);

		assertTrue(result.firstEntry().getValue() == 109.93f);
		assertTrue(result.lastEntry().getValue() == 65.07f);

		result = Utils.parsePrices(XML, "PT60M", currencyExchangeValue);

		assertFalse(result.firstEntry().getValue() == 109.93f);
		assertTrue(result.lastEntry().getValue() == 86.53f);

		var res = Utils.parseCurrency(XML);
		assertEquals(res, Currency.EUR.toString());
	}
}
