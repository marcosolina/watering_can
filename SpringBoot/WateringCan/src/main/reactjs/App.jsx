import React, {Component} from 'react';
import ReactDOM from 'react-dom';
import Pump from './components/Pump.jsx';
import { doHttpRequest } from './utils/UtilsFunctions.jsx';
import Grid from '@material-ui/core/Grid';
import InputText from './components/InputText.jsx';
import Fab from '@material-ui/core/Fab';
import SaveIcon from '@material-ui/icons/Save';
import Snackbar from '@material-ui/core/Snackbar';
import IconButton from '@material-ui/core/IconButton';
import CloseIcon from '@material-ui/icons/Close';
import MlSlider from './components/MlSlider.jsx';
import Card from '@material-ui/core/Card';
import CardActions from '@material-ui/core/CardActions';
import CardContent from '@material-ui/core/CardContent';

class App extends Component{
	constructor(props) {
		super(props);
		this.state = {
			pumps: [],
			snackOpen: false,
			snackMessage: ""
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
			/* TODO return just the status. Otherwise I will overwrite the Description every time
			setTimeout(this.retrieveListOfPumps.bind(this), 2000);
			*/
        }
	}


	onChangePump(mac, id, status){
		doHttpRequest(__URLS.ACTIONS.SET_PUMP_STATUS, 'POST', {
			mac: mac,
			id: id,
			status: status   
		   }, this.statusUpdateComplete.bind(this));
	}

	statusUpdateComplete(resp){
        if (resp.status) {
            //TODO show an error or just ignore?
        }
	}
	
	onChangeInput(id, newValue){
		let pumps = this.state.pumps;
		let info = id.split("_");

		for(let i = 0; i < pumps.length; i++){
			let pump = pumps[i];
			if(pump.mac == info[0] && pump.id == info[1]){
				pumps[i].description = newValue;
				break;
			}
		}
		this.setState({pumps});
	}

	saveConfig(){
		doHttpRequest(__URLS.ACTIONS.SAVE_CONFIG, 'POST', {
			pumps: this.state.pumps
		}, this.onConfigSaved.bind(this));
	}

	onConfigSaved(resp){
		let message = "";
		if(resp.status){
			message = "Configuration Saved!!!";
		}else{
			message = "Configuration not saved!!!";
		}
		this.setState({snackOpen: true, snackMessage: message});
	}

	closeSnackBar(){
		this.setState({snackOpen: false, snackMessage: ""});
	}

	onChangeSlider(){
		
	}


	render(){

		let pumps = this.state.pumps;

		return(
				<Grid container spacing={3}>
					{
						pumps.map((pump, index) => {
							return  <Grid item xs={12} sm={6} md={4} lg={3} xl={2} key={index}>
										<Card>
											<CardContent>
												<InputText 
													name={"" + pump.mac + "_" + pump.id}
													id={"" + pump.mac + "_" + pump.id}
													value={pump.description}
													label={"Plant"}
													onChange={this.onChangeInput.bind(this)}
												/>
												<br/><br/>
												<MlSlider 
													mac={pump.mac}
													id={pump.id}
													onChange={this.onChangeSlider.bind(this)}
												/>
											</CardContent>
											<CardActions>
												<Pump
													mac={pump.mac}
													id={pump.id}
													status={pump.status}
													onChange={this.onChangePump.bind(this)}
												/>
											</CardActions>
										</Card>
									</Grid>
						})
					}
					<Grid item xs={12}>
						<Fab color="primary" aria-label="save" onClick={this.saveConfig.bind(this)}>
							<SaveIcon />
						</Fab>
					</Grid>
					<Grid item>
						<Snackbar
							anchorOrigin={
								{
									vertical: 'bottom',
									horizontal: 'left',
								}
							}
							open={this.state.snackOpen}
							autoHideDuration={3000}
							onClose={() => this.setState({snackOpen: false})}
							message={this.state.snackMessage}
							action={
								<IconButton size="small" aria-label="close" color="inherit" onClick={this.closeSnackBar.bind(this)}>
									<CloseIcon fontSize="small" />
								</IconButton>
							}
						/>
					</Grid>
				</Grid>
		);
	}
}

export default App;

ReactDOM.render(<App />, document.getElementById("reactapp"));