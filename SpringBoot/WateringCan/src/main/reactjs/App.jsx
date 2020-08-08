import React, {Component} from 'react';
import ReactDOM from 'react-dom';
import Pump from './components/Pump.jsx';
import { doHttpRequest } from './utils/UtilsFunctions.jsx';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from "@material-ui/core/ListItemText";
import ListItemSecondaryAction from "@material-ui/core/ListItemSecondaryAction";
import Grid from '@material-ui/core/Grid';

class App extends Component{
	constructor(props) {
		super(props);
		this.state = {
			pumps: []
		};
	}

	componentDidMount() {
		this.retrieveListOfPumps();
	}

	retrieveListOfPumps() {
		doHttpRequest(__URLS.ACTIONS.LIST_PUMPS, 'GET', undefined, this.retrieveListOfPumpsComplete.bind(this));
	}
	
	retrieveListOfPumpsComplete(resp){
		console.log(resp);
        if (resp.status && resp.pumps) {
			this.setState({pumps: resp.pumps});
			setTimeout(this.retrieveListOfPumps.bind(this), 2000);
        }
	}


	onChangePump(ip, id, status){
		doHttpRequest(__URLS.ACTIONS.SET_PUMP_STATUS, 'POST', {
			ip: ip,
			id: id,
			status: status   
		   }, this.statusUpdateComplete.bind(this));
	}

	statusUpdateComplete(resp){
        if (resp.status) {
            //MarcoUtils.showNotification({title: "Ok", message: "Command executed", type: __NOTIFY_TYPE.INFO});
        }
    }


	render(){

		let pumps = this.state.pumps;

		return(
				<Grid container spacing={3}>
					<Grid item xs={12} sm={6} md={4} lg={2} xl={1}>
						<List>
							{
								pumps.map((pump, index) => {
									return <ListItem key={index}>
											<ListItemText primary={"" + pump.ip + "_" + pump.id}/>
											<ListItemSecondaryAction>
												<Pump
													ip={pump.ip}
													id={pump.id}
													status={pump.status}
													onChange={this.onChangePump.bind(this)}
												/>
											</ListItemSecondaryAction>
										</ListItem>
								})
							}
						</List>
					</Grid>
				</Grid>
				
		);
	}
}

export default App;

ReactDOM.render(<App />, document.getElementById("reactapp"));