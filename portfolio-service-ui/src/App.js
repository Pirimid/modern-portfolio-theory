import React, { useState } from 'react';

import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Table from 'react-bootstrap/Table';
import Button from 'react-bootstrap/Button';
import './App.css';
import { Form, InputGroup } from 'react-bootstrap';
import Modal from "react-bootstrap/Modal";
import { Typeahead } from 'react-bootstrap-typeahead';
import { searchQuote, getQuotes, optimizePortfolio } from './API';
import { arrayUnion } from './UTIL';
import 'react-bootstrap-typeahead/css/Typeahead.css';
import { FaMinusCircle, FaChartLine, FaIdCard }  from 'react-icons/fa';
import RiskProfiler from './RiskProfiler';
import pirimidLogoImage from './pirimid-logo.png';

const App = () => {
  
  const [expandedRows, setExpandedRows] = useState([]);

  const [expandState, setExpandState] = useState({});

  let [data, setData] = useState([]);

  const [multiSelections, setMultiSelections] = useState([]);

  const [options, setOptions] = useState([]);

  const [fromDate, setFromDate] = useState("2008-06-01");

  const [toDate, setToDate] = useState((new Date()).toISOString().split('T')[0]);

  const [totalInvestmentAmount, setTotalInvestmentAmount] = useState(0.0);

  const [assetConstraintsShown, setAssetConstraintsShown] = useState(false);

  const [selectedCountry, setSelectedCountry] = useState('IN');

  const [selectedAssetType, setSelectedAssetType] = useState('TICKER');

  const [showRiskProfilerModal, setShowRiskProfilerModal] = useState(true);

  const [showRiskProilerSteps, setShowRiskProilerSteps] = useState(false);

  const handleEpandRow = (event, assetId) => {
    const currentExpandedRows = expandedRows;
    const isRowExpanded = currentExpandedRows.includes(assetId);

    let obj = {};
    isRowExpanded ? (obj[assetId] = false) :  (obj[assetId] = true);
    setExpandState(obj);

    const newExpandedRows = isRowExpanded ?
          currentExpandedRows.filter(id => id !== assetId) :
          currentExpandedRows.concat(assetId);

    setExpandedRows(newExpandedRows);
  }

  const closeRiskProfileDialog = () => {
    setShowRiskProfilerModal(false);
    setShowRiskProilerSteps(false);
  }

  return(
    <>
    <Container>
      {/* <Row>
        <Col>
          <img src={pirimidLogoImage} alt='' width='5%' />
        </Col>
      </Row> */}
      <Row>
      </Row>
      <Row className="mt-2">
        <Col sm={3}>
          <Form.Control as="select" onChange={(event) => {setSelectedCountry(event.target.value); setSelectedAssetType('TICKER'); setOptions([]); setMultiSelections([]); setData([]);}}>
              <Form.Label as="option" value="IN">India</Form.Label>
              <Form.Label as="option" value="US">U.S.</Form.Label>
          </Form.Control>
        </Col>
        <Col sm={3}>
          <Form.Control as="select" 
            onChange={(event) => {
              const assetType = event.target.value;
              if(assetType === 'INDEX') {
                searchQuote('', selectedCountry,assetType)
                .then(response => (response.json().then(items => setOptions(items.map(item => {
                  let mappedItem = {
                    ...item,
                    companyName:  item.name,
                    name: `${item.name} (${item.symbol})`,
                  }
                  return mappedItem;
                })))));
              }
              setSelectedAssetType(assetType); 
              setMultiSelections([]);
              setOptions([]); 
              setData([]);
            }} 
            value={selectedAssetType}
            >
              <Form.Label as="option" value="TICKER">Ticker</Form.Label>
              <Form.Label as="option" value="INDEX">Index</Form.Label>
          </Form.Control>
        </Col>
      </Row>
      
      <Form.Group style={{ marginTop: '20px' }}>
      <Form.Label>Select {selectedAssetType === 'TICKER' ? 'Tickers' : 'Indexes'} (Atleast 3)</Form.Label>
          <Typeahead
            id="basic-typeahead-multiple"
            labelKey="name"
            multiple
            onChange={setMultiSelections}
            options={options}
            onInputChange={
              (searchTerm) => {
                if(searchTerm) {
                  searchQuote(searchTerm, selectedCountry, selectedAssetType)
                  .then(response => (response.json().then(items => setOptions(items.map(item => {
                    let mappedItem = {
                      ...item,
                      companyName:  item.name,
                      name: `${item.name} (${item.symbol})`,
                    }
                    return mappedItem;
                  })))));
                }
              }
            }
            placeholder={selectedAssetType === 'TICKER' ? 'Add Atleast 3 Chars' : ''}
            selected={multiSelections}
          />
      </Form.Group>
      
      <Row>
        <Col>
          <Button  onClick={() => {
            const newData = arrayUnion(data, multiSelections,function(obj1, obj2) {return obj1.symbol === obj2.symbol});
              setMultiSelections([]);
              if(selectedAssetType === 'TICKER') {
                getQuotes(newData.map(item => item.symbol)).then(response => (response.json().then(
                  items => setData(Object.values(items).map(item => {
                    item.minWeight = 0;
                    item.maxWeight = 100;
                    return item;
                  }))
                )));
              } else {
                setData(newData.map(item => {
                    item.minWeight = 0;
                    item.maxWeight = 100;
                    return item;
                }));
              }
            }}
            disabled={multiSelections.length <= 0 }
            >Add All To Portfolio</Button>
        </Col>
      </Row>
      <br />
      <Row>
        <Col style={{borderLeft: '2px black solid'}}>
          <Row>
            <Col>
              <Form.Group>
                <Form.Label sm="1">From</Form.Label>
                <Form.Control type="date" onChange = {(event) => setFromDate(event.target.value)} defaultValue="2008-06-01" min="2008-06-01"/>
              </Form.Group>
            </Col>
            <Col>
              <Form.Group>
                <Form.Label sm="1">To</Form.Label>
                <Form.Control type="date" onChange = {(event) => setToDate(event.target.value)} max={(new Date()).toISOString().split('T')[0]} defaultValue={(new Date()).toISOString().split('T')[0]} />
              </Form.Group>
            </Col>
          </Row>
        </Col>
        <Col></Col>
      </Row>
      <br />
      <Row>
        <Col style={{borderLeft: '2px black solid'}}>
          <Row>
            <Col>
              <InputGroup size="lg" className="mb-2">
                <InputGroup.Prepend>
                  <InputGroup.Text id="basic-addon1">Investment Amount</InputGroup.Text>
                </InputGroup.Prepend>
                <Form.Control
                  aria-label="InvestmentAmount"
                  aria-describedby="InvestmentAmount"
                  type="number"
                  onChange={(event) => setTotalInvestmentAmount(event.target.value)}
                />
              </InputGroup>
            </Col>
          </Row>
        </Col>
        <Col></Col>
      </Row>
      <br />
      <Row>
        <Col>
          <Row>
            <Col>
              <Form.Check 
                type="checkbox" 
                label="Asset Constraints" 
                onClick={() => {
                    if(assetConstraintsShown) {
                      data.forEach(dataItem => {dataItem.minWeight = 0; dataItem.maxWeight = 100});
                      setData([...data]);
                    }
                    setAssetConstraintsShown(!assetConstraintsShown);
                }}/>
            </Col>
          </Row>
        </Col>
      </Row>
      
      {
        data.length > 2 ?
        <Row>
          <Col>
            <br />
            <Button 
                  variant="danger" 
                  onClick={() => {
                    let promiseResponse = null;
                    let requestData = data.map(item => {
                      return {
                        symbol : item.symbol, 
                        minWeight: (parseFloat(item.minWeight)/100), 
                        maxWeight: (parseFloat(item.maxWeight)/100)
                      };
                    });
                    promiseResponse = optimizePortfolio(selectedAssetType, selectedCountry, fromDate, toDate,requestData);
                    promiseResponse.then(
                      response => response.json()
                      .then(
                        items => {
                          const assetData = items.assetData;
                          let newData = [];
                          
                          Object.keys(assetData).forEach(key => {
                            let dataItem = data.find(dataItem => key === dataItem.symbol);
                            const bestWeight = parseFloat(assetData[key].bestWeight.toFixed(2));
                            dataItem.bestWeight = (bestWeight * 100)  + '%';
                            dataItem.bestInvestmentAmount = (totalInvestmentAmount * bestWeight).toFixed(2);
                            dataItem.returns = assetData.returns;
                            dataItem.meanReturn = assetData.meanReturn;
                            newData.push(dataItem);
                          });
                          data = newData;
                          setData([...data])
                    }))
                  }}
                ><h5><FaChartLine />Optimize Portfolio</h5>
              </Button>
          </Col>
        </Row> : null
      }
      
      
      <br />
      <Row>
        <Col>
          <Row><Col><h1> Portfolio Assets({ data.length })</h1></Col></Row>
        </Col>
      </Row>
      <Row>
        <Col sm={12}>
          <Table responsive variant="dark">
            <thead>
                <tr>
                  <th>Script</th>
                  {
                    assetConstraintsShown ? <th>Min. Weight(%)</th> : null
                  }
                  {
                    assetConstraintsShown ? <th>Max. Weight(%)</th> : null
                  }
                  <th>Best Weight</th>
                  <th>Amount</th>
                </tr>
            </thead>
            <tbody>
            {
              data.map((asset) =>
              <>
                <tr key={asset.symbol}>
                    <td>
                      {`${asset.name} (${asset.symbol})`}
                    </td>
                    {
                      assetConstraintsShown ?
                      <td>
                        <Form.Control 
                          type="number" 
                          max={100} min={0} 
                          defaultValue={0} 
                          onChange={(event) => {asset.minWeight = event.target.value;}}
                        />
                      </td> : null
                    }
                    {
                      assetConstraintsShown ?
                      <td>
                        <Form.Control 
                          type="number" 
                          max={100} min={0} 
                          defaultValue={100} 
                          onChange={(event) => {asset.maxWeight = event.target.value;}}
                        />
                      </td> : null
                    }
                    
                    
                    <td>{asset.bestWeight ? `${asset.bestWeight}` : '-'}</td>
                    <td>{asset.bestInvestmentAmount ? `${asset.bestInvestmentAmount}` : '-'}</td>
                    {
                      selectedAssetType === 'TICKER' ? 
                        <td>
                          <Button
                              variant="link"
                              onClick={event => handleEpandRow(event, asset.symbol)}>
                              {
                                expandState[asset.symbol] ?
                                  'Hide' : 'Details'
                              }
                          </Button>
                      </td>:
                      null
                    }
                    
                    <td><FaMinusCircle color={'red'} onClick={() => {
                      let index = data.findIndex(value => value.symbol === asset.symbol)
                      data.splice(index,1)
                      setData([...data])
                    }} /></td>
                </tr>
                <>
                {
                  expandedRows.includes(asset.symbol) ?
                  <tr key={asset.symbol}>
                    <td colSpan="1">
                      <div style={{backgroundColor: '#343A40', color: '#FFF', padding: '10px'}}>
                        <ul>
                          <li>
                            <span><b>Symbol:</b></span> {' '}
                            <span>{ asset.symbol }</span>
                          </li>
                          <li>
                            <span><b>Name:</b></span> {' '}
                            <span> { asset.name } </span>
                          </li>
                          <li>
                            <span><b>Currency:</b></span> {' '}
                            <span> { asset.currency } </span>
                          </li>
                          <li>
                            <span><b>Stock Exchange:</b></span> {' '}
                            <span> { asset.stockExchange } </span>
                          </li>
                        </ul>
                      </div>
                    </td>
                    <td colSpan="2">
                      <div style={{backgroundColor: '#343A40', color: '#FFF', padding: '10px'}}>
                        <ul>
                          <li>
                            <span><b>Year Low:</b></span> {' '}
                            <span>{ asset.quote.yearLow }</span>
                          </li>
                          <li>
                            <span><b>Year High:</b></span> {' '}
                            <span> { asset.quote.yearHigh } </span>
                          </li>
                          <li>
                            <span><b>Avg. Price (50 Days):</b></span> {' '}
                            <span> { `${asset.quote.priceAvg50}` } </span>
                          </li>
                          <li>
                            <span><b>Avg. Price (200 Days):</b></span> {' '}
                            <span> { `${asset.quote.priceAvg200}` } </span>
                          </li>
                        </ul>
                      </div>
                    </td>
                    <td colSpan="3">
                      <div style={{backgroundColor: '#343A40', color: '#FFF', padding: '10px'}}>
                        <ul>
                          <li>
                            <span><b>Curr. Price compared to Yr High:</b></span> {' '}
                            <span> { `${asset.quote.changeFromYearHighInPercent}%` } </span>
                          </li>
                          <li>
                            <span><b>Curr. Price compared to Yr Low:</b></span> {' '}
                            <span> { `${asset.quote.changeFromYearLowInPercent}%` } </span>
                          </li>
                          <li>
                            <span><b>Curr. Price compared to 50d Avg.:</b></span> {' '}
                            <span> { `${asset.quote.changeFromAvg50InPercent}%` } </span>
                          </li>
                          <li>
                            <span><b>Curr. Price compared to 200d Avg:.</b></span> {' '}
                            <span> { `${asset.quote.changeFromAvg200InPercent}%` } </span>
                          </li>
                        </ul>
                      </div>
                    </td>
                  </tr> : null
                }
                </>
              </> 
              )}
            </tbody>
          </Table>
       </Col>
      </Row>
      {
        !showRiskProfilerModal ?
        (
          <FaIdCard 
            fontSize={60}
            color='white'
            className='floating-menu'
            onClick={() => setShowRiskProfilerModal(true)}
          />
        ) :
          null
        
      }
      
    </Container>
    <Modal show={showRiskProfilerModal} >
      {
        showRiskProilerSteps ?
        (
          <>
            <RiskProfiler 
              steps={
                
                [
                  {
                    title: 'Financial',
                    questions: [
                      {
                        question: 'How much is your net worth ?',
                        answers: [
                          {
                            'answer': 'Having only Liabilities (Negative Net-Worth)',
                            'score': 1,
                          },
                          {
                            'answer': 'Upto 12 times Monthly Expenses',
                            'score': 2,
                          },
                          {
                            'answer': '13-36 times Monthly Expenses',
                            'score': 3,
                          },
                          {
                            'answer': '37-48 times Monthly Expenses',
                            'score': 4,
                          },
                          {
                            'answer': '49-60 times Monthly Expenses',
                            'score': 5,
                          }
                        ]
                      },
                      {
                        question: 'How much is your Income Saving Rate ?',
                        answers: [
                          {
                            'answer': 'Upto 5%',
                            'score': 1,
                          },
                          {
                            'answer': '6-15%',
                            'score': 2,
                          },
                          {
                            'answer': '16-25%',
                            'score': 3,
                          },
                          {
                            'answer': '26-50%',
                            'score': 4,
                          },
                          {
                            'answer': '51-75%',
                            'score': 5,
                          }
                        ]
                      },
                      {
                        question: 'How many people are dependent on you ?',
                        answers: [
                          {
                            'answer': 'No Dependency',
                            'score': 5,
                          },
                          {
                            'answer': '1 Dependency',
                            'score': 4,
                          },
                          {
                            'answer': '2 Dependencies',
                            'score': 3,
                          },
                          {
                            'answer': '3 Dependencies',
                            'score': 2,
                          },
                          {
                            'answer': '4 or More Dependencies',
                            'score': 1,
                          }
                        ]
                      },
                      {
                        question: 'What is the Consistency of your job and your income ?',
                        answers: [
                          {
                            'answer': 'High',
                            'score': 3,
                          },
                          {
                            'answer': 'Moderate',
                            'score': 2,
                          },
                          {
                            'answer': 'Low',
                            'score': 1,
                          },
                        ]
                      }
                    ],
                    type: 'questionnaire'
                  }, 
                  {
                    title: 'Psychological',
                    questions: [
                      {
                        question: 'What\'s your level of expertise in the Share Market ?',
                        answers: [
                          {
                            'answer': 'No Knowledge',
                            'score': 1
                          },
                          {
                            'answer': 'Beginner Level',
                            'score': 2
                          },
                          {
                            'answer': 'Intermediate Level',
                            'score': 3
                          },
                          {
                            'answer': 'Expert Level',
                            'score': 4
                          },
                          {
                            'answer': 'Professional Level',
                            'score': 5
                          }
                        ]
                      },
                      {
                        question: 'Considering an investment of 2 Lakh, how much fall can you tolerate in one month ?',
                        answers: [
                          {
                            'answer': '0-5%, upto 10,000',
                            'score': 1
                          },
                          {
                            'answer': '6-10%, between 10,000 to 20,000',
                            'score': 2
                          },
                          {
                            'answer': '11-20%, between 20,000 to 40,000',
                            'score': 3
                          },
                          {
                            'answer': '21-30%, between 40,0000 to 60,000',
                            'score': 4
                          },
                          {
                            'answer': 'More than 30%, 60,000+',
                            'score': 5
                          }
                        ]
                      },
                      {
                        question: 'What\'s the maximum amount of time for which you can tolerate loss ?',
                        answers: [
                          {
                            'answer': 'Less than 3 months',
                            'score': 1
                          },
                          {
                            'answer': 'Between 4 months - 1 Year',
                            'score': 2
                          },
                          {
                            'answer': 'Between 1 - 2 Years',
                            'score': 3
                          },
                          {
                            'answer': 'Between 2 - 3 Years',
                            'score': 4
                          },
                          {
                            'answer': 'Between 3 - 5 Years',
                            'score': 5
                          }
                        ]
                      },
                    ],
                    type: 'questionnaire'
                  }, 
                  {
                    title: 'Assessment',
                    type: 'result',
                  }, 
                ] 
            
            }
              closeRiskProfileDialog={closeRiskProfileDialog}
            />
          </>
        ):
        (
          <>
            <Modal.Header>
              <h5>
                Want to Have a Risk Profile ?
              </h5>
            </Modal.Header>
            <Modal.Body>
              <p style={{overflowWrap: 'break-word'}}>
                Analysing one’s risk is an important factor before investing. 
                We have designed a Test which can help you in identifying your risk-taking capacity and your risk-tolerance level. 
                <br />
                <br />
                This test has been crafted by our expert analysts, and involves a total of 7 questions divided into two parts. 
                The first part checks your financial risk so that you can understand how much risk you can take. 
                And the second part checks how much risk you can tolerate, that is, your psychological risk tolerance level.
                <br />
                <br />
                Each part awards you with points based on the options you choose in the questions and their total will be calulated
                and compared against Risk Bands which will help you gauge your Financial and Psychological risk.
              </p>
            </Modal.Body>
            <Modal.Footer>
              <Button variant="primary" onClick={() => setShowRiskProilerSteps(true)}>Start</Button>
              {'   '}
              <Button variant="secondary" onClick={() => setShowRiskProfilerModal(false)}>Close</Button>
            </Modal.Footer>
          </>
        )
      }
      
    </Modal>
    </>
  )
};

export default App;