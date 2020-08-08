import React, {Component} from 'react';
import ReactDOM from 'react-dom';

class App extends Component{
	render(){
		return(
				<div>
					<h1>Hello from Watering Can</h1>
				</div>
		);
	}
}

export default App;

ReactDOM.render(<App />, document.getElementById("reactapp"));