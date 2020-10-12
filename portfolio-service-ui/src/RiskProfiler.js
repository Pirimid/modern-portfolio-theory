import React from 'react';
import Stepper from 'react-stepper-horizontal';
import { Button, Card, Modal } from 'react-bootstrap';
import { Form } from 'react-bootstrap';
import riskProfileScaleImage from './risk-profile-scale.png';



export default class RiskProfiler extends React.Component {


  constructor(props) {
    super(props);
    this.state = {
      activeRiskProfilerStep: 0,
      steps: props.steps
    };
  }

  getStepContentComponent(step) {
    switch(step.type) {
      case 'questionnaire': {
        return (
          step.questions
            .map((question,index) => <Card key={step.title + index}>
                              <Card.Body>
                                <Form.Label>{question.question}</Form.Label>
                                <Form.Control key={question.question} as="select" value={question.score} onChange={(event) => {
                                  let steps = this.state.steps;
                                  let st = steps[this.state.activeRiskProfilerStep];
                                  let ques = st.questions[index];
                                  ques.score = event.target.value;
                                  st.questions[index] = ques;
                                  steps[this.state.activeRiskProfilerStep] = st;
                                  this.setState({steps: [...steps]});
                                }}>
                                    <option style={{display: 'none'}} />
                                    {
                                      question.answers.map(answer => <option value={answer.score}>{answer.answer}</option>)
                                    }
                                </Form.Control>
                              </Card.Body>
                            </Card>
            )
        );
      }
      case 'result': {
        return (
          <Card>
          <Card.Body>
            <img src={riskProfileScaleImage} alt="" width='100%'/>
            <br /><br />
            {
              this.state.steps.filter(step => step.type === 'questionnaire').map(step => {
                return(
                  <Card.Header>
                    <Card.Title>Your {step.title} Risk Score</Card.Title>
                    <Card.Subtitle>{step.questions.map(question => parseInt(question.score)).reduce((prev, curr) => prev + curr)}</Card.Subtitle>
                  </Card.Header>
                );
              })
            }
          </Card.Body>
        </Card>
        );
      }
      default: {
        return null;
      }
    }
      
  }

  render() {
    return (
      <>
      <Modal.Body>
        <Stepper 
          steps={ this.state.steps } 
          activeStep={this.state.activeRiskProfilerStep} 
          activeColor="#000000"
          completeColor="#000000"
        />
        <div>  
          {
            this.getStepContentComponent(this.state.steps[this.state.activeRiskProfilerStep])
          }
        </div>
        </Modal.Body>
        <Modal.Footer>
          
          {
            this.state.activeRiskProfilerStep > 0 ? 
            (<Button variant="primary" onClick={() => this.setState({activeRiskProfilerStep: this.state.activeRiskProfilerStep - 1})}>Back</Button>) :
            null
          }
          {'   '}
          {
            this.props.steps.length - 1 > this.state.activeRiskProfilerStep ? 
            (<Button variant="primary" onClick={() => this.setState({activeRiskProfilerStep: this.state.activeRiskProfilerStep + 1})}>Next</Button>) :
            null
          }
          {'   '}
          <Button variant="secondary" onClick={() => this.props.closeRiskProfileDialog()}>Close</Button>{'   '}
        </Modal.Footer>
        </>
    );
  }

}