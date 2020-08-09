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
			pumps: {},
			snackOpen: false,
			snackMessage: "",
			firstLoad: true
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
        if (resp.status && resp.pumps && resp.pumps.length > 0) {
			if(this.state.firstLoad){
				const pumps = {};
				for(let i = 0; i < resp.pumps.length; i++){
					pumps[resp.pumps[i].mac + "_" + resp.pumps[i].id] = resp.pumps[i];
				}
				this.setState({pumps: pumps, firstLoad: false});
			}else{
				const p = {...this.state.pumps};
				for(let i = 0; i < resp.pumps.length; i++){
					p[resp.pumps[i].mac + "_" + resp.pumps[i].id].status = resp.pumps[i].status;
				}
				this.setState({pumps: p});
			}
			setTimeout(this.retrieveListOfPumps.bind(this), 2000);
        }
	}


	onChangePump(mac, id, status){
		let pumps = this.state.pumps;
		let pumpKey = mac + "_" + id;
		pumps[pumpKey].status = status;
		pumps[pumpKey].forceStatus = true;
		doHttpRequest(__URLS.ACTIONS.SET_PUMP_STATUS, 'POST', pumps[pumpKey], this.statusUpdateComplete.bind(this));
		this.setState({pumps});
	}

	statusUpdateComplete(resp){
        if (resp.status) {
            //TODO show an error or just ignore?
        }
	}
	
	onChangeInput(id, newValue){
		let pumps = this.state.pumps;

		let pumpKey = id;
		pumps[pumpKey].description = newValue;

		this.setState({pumps: pumps});
	}

	saveConfig(){
		let arrPumps = [];
		for (const property in this.state.pumps) {
			arrPumps.push(this.state.pumps[property]);
		}

		doHttpRequest(__URLS.ACTIONS.SAVE_CONFIG, 'POST', {
			pumps: arrPumps
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

	onChangeSlider(mac, id, value){
		let pumps = this.state.pumps;

		let pumpKey = mac + "_" + id;
		pumps[pumpKey].ml = value;
		pumps[pumpKey].forceStatus = false;

		this.setState({pumps});
	}


	render(){

		let arrPumps = [];
		for (const property in this.state.pumps) {
			arrPumps.push(this.state.pumps[property]);
		}

		return(
				<Grid container spacing={3}>
					{
						arrPumps.map((pump, index) => {
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
													value={pump.ml}
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