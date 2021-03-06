import React, {Component} from 'react';
import ReactDOM from 'react-dom';
import PumpSwitch from './components/PumpSwitch.jsx';
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
import OpacityIcon from '@material-ui/icons/Opacity';
import HumidityConfigDialog from './components/HumidityConfigDialog.jsx';
import Tooltip from '@material-ui/core/Tooltip';
import LocalGasStationIcon from '@material-ui/icons/LocalGasStation';
import HumidtyInfo from './components/HumidityInfo.jsx';
import PumpCalibrationDialog from './components/PumpCalibrationDialog.jsx';

class App extends Component{
	constructor(props) {
		super(props);
		this.state = {
			pots: {},
			snackOpen: false,
			snackMessage: "",
			firstLoad: true,
			dialogOpen: false,
			pumpDialogOpen: false,
			potConfig: {},
			autoRefresh: true
		};
	}

	componentDidMount() {
		this.retrieveListOfPots();
	}

	retrieveListOfPots() {
		doHttpRequest(__URLS.ACTIONS.LIST_POTS, 'GET', undefined, this.retrieveListOfPotsComplete.bind(this));
	}
	
	retrieveListOfPotsComplete(resp){
		console.log(resp);
        if (resp.status && resp.pots && resp.pots.length > 0) {
			if(this.state.firstLoad){
				const pots = {};
				for(let i = 0; i < resp.pots.length; i++){
					pots[resp.pots[i].mac + "_" + resp.pots[i].id] = resp.pots[i];
				}
				this.setState({pots: pots, firstLoad: false});
			}else{
				/*
				*	I update only the things that I am interested
				*/
				const p = {...this.state.pots};
				for(let i = 0; i < resp.pots.length; i++){
					let pot = resp.pots[i];
					let potMacAndId = pot.mac + "_" + pot.id;
					p[potMacAndId].status = pot.status;
					p[potMacAndId].humidity = pot.humidity;
					p[potMacAndId].maxHumidityRead = pot.maxHumidityRead;
					p[potMacAndId].minHumidityRead = pot.minHumidityRead;
					p[potMacAndId].moistureCurRead = pot.moistureCurRead;
				}
				this.setState({pots: p});
			}
			if(this.state.autoRefresh){
				setTimeout(this.retrieveListOfPots.bind(this), 2000);
			}
        }
	}


	/**
	 * Send the new status that I want to set
	 * @param {*} mac 
	 * @param {*} id 
	 * @param {*} status 
	 */
	onChangePump(mac, id, status){
		let pots = this.state.pots;
		let potKey = mac + "_" + id;
		pots[potKey].status = status;
		pots[potKey].forceStatus = true;
		doHttpRequest(__URLS.ACTIONS.SET_PUMP_STATUS, 'POST', pots[potKey], this.statusUpdateComplete.bind(this));
		this.setState({pots});
	}

	statusUpdateComplete(resp){
        if (resp.status) {
            //TODO show an error or just ignore?
        }
	}
	
	/**
	 * Updating the desciption input text
	 * @param {*} id 
	 * @param {*} newValue 
	 */
	onChangeInput(id, newValue){
		let pots = this.state.pots;

		let potKey = id;
		pots[potKey].description = newValue;

		this.setState({pots: pots});
	}

	/**
	 * Save the config of for all the Flower Pots
	 */
	saveConfig(){
		let arrPots = [];
		for (const property in this.state.pots) {
			arrPots.push(this.state.pots[property]);
		}

		doHttpRequest(__URLS.ACTIONS.SAVE_CONFIG, 'POST', {
			pots: arrPots
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

	openDialog(potId){

		this.setState({
			dialogOpen: true,
			potConfig: this.state.pots[potId],
			potConfigId: potId
		});
	}

	/**
	 * Updating the ml when the slider is changed by the user
	 * @param {*} mac 
	 * @param {*} id 
	 * @param {*} value 
	 */
	onChangeSlider(mac, id, value){
		let pots = this.state.pots;

		let potKey = mac + "_" + id;
		pots[potKey].ml = value;
		pots[potKey].forceStatus = false;

		this.setState({pots});
	}

	onCancelDialog(){
		this.setState({dialogOpen: false});
	}

	/**
	 * Send the new config to Arduino
	 */
	onSaveDialog(){
		this.setState({dialogOpen: false});
		doHttpRequest(__URLS.ACTIONS.SET_WET_DRY, 'POST', this.state.potConfig, this.onConfigSaved.bind(this));
	}

	/**
	 * Updating the state when the user changes the "Wet" or "Dry" values
	 * @param {*} id 
	 * @param {*} newValue 
	 */
	onChangeInputDialog(id, newValue){
		let newState = {...this.state};
		switch(id){
		case "dry":
			newState.potConfig.dryValue = newValue;
			newState.pots[newState.potConfigId].dryValue = newValue;
			break;
		case "wet":
			newState.potConfig.wetValue = newValue;
			newState.pots[newState.potConfigId].wetValue = newValue;
			break;
		}

		this.setState(newState);
	}


	openPumpDialog(potId){

		this.setState({
			pumpDialogOpen: true,
			autoRefresh: false,
			potConfig: this.state.pots[potId],
			potConfigId: potId
		});
	}

	onCancelPumpDialog(){
		this.setState({
			pumpDialogOpen: false,
			autoRefresh: true
		}, this.retrieveListOfPots.bind(this));
	}

	onSavePumpDialog(){
		let pots = {...this.state.pots};
		let potKey = this.state.potConfig.mac + "_" + this.state.potConfig.id;
		pots[potKey] = this.state.potConfig;
		this.setState({
			pumpDialogOpen: false,
			autoRefresh: true,
			pots: pots
		}, this.retrieveListOfPots.bind(this));
	}

	onChangePumpInDialog(mac, id, status){
		let pot = {...this.state.potConfig};
		let pot2 = {...this.state.potConfig};
		pot.status = status;
		pot.ml = -1;//I will let the user decide when to stop the pump
		pot2.status = status;

		let newState = {};

		if(status == "ON"){
			pot2.start = new Date();
		}else{
			let timeDiff = new Date() - pot2.start;
			let seconds = Math.round(timeDiff / 1000);
			let tmpMlPerSecong = 100;
			if(seconds > 0){
				tmpMlPerSecong = Math.round(100 / seconds);
			}
			pot2.mlPerSecond = tmpMlPerSecong;  
			newState.snackOpen = true;
			newState.snackMessage = "This pump pours " + pot2.mlPerSecond + "ml per second";
		}
		newState.potConfig = pot2;
		doHttpRequest(__URLS.ACTIONS.SET_PUMP_STATUS, 'POST', pot, this.statusUpdateComplete.bind(this));
		this.setState(newState);
	}


	render(){

		let arrPots = [];
		for (const property in this.state.pots) {
			arrPots.push(this.state.pots[property]);
		}

		return(
				<React.Fragment>
					<Grid container spacing={3}>
						{
							arrPots.map((pot, index) => {
								return  <Grid item xs={12} sm={6} md={4} lg={3} xl={2} key={index}>
											<Card>
												<CardContent>
													<Grid container>
														<Grid item xs={12} container justify="flex-end">
															<HumidtyInfo value={pot.humidity} />
														</Grid>
														<Grid item xs={12}>
															<InputText 
																name={"" + pot.mac + "_" + pot.id}
																id={"" + pot.mac + "_" + pot.id}
																value={pot.description}
																label={"Plant"}
																onChange={this.onChangeInput.bind(this)}
																/>
														</Grid>
														<Grid item xs={12}>
															<br/>
															<MlSlider 
																mac={pot.mac}
																id={pot.id}
																value={pot.ml}
																onChange={this.onChangeSlider.bind(this)}
															/>		
														</Grid>
													</Grid>
												</CardContent>
												<CardActions>
													<Grid container justify="space-between" alignItems="center">
														<Grid item justify="flex-start">
															<Tooltip title="Calibrate the Moisture Sensor" placement="top">
																<IconButton 
																	color="primary" 
																	component="span"
																	onClick={this.openDialog.bind(this, "" + pot.mac + "_" + pot.id)}
																>
																	<OpacityIcon />
																</IconButton>
															</Tooltip>
															<Tooltip title="Calibrate the Water pump" placement="top">
																<IconButton 
																	color="primary" 
																	component="span"
																	onClick={this.openPumpDialog.bind(this, "" + pot.mac + "_" + pot.id)}
																>
																	<LocalGasStationIcon />
																</IconButton>
															</Tooltip>
														</Grid>
														<Grid item justify="flex-end">
															<PumpSwitch
																mac={pot.mac}
																id={pot.id}
																status={pot.status}
																onChange={this.onChangePump.bind(this)}
															/>
														</Grid>
													</Grid>
												</CardActions>
											</Card>
										</Grid>
							})
						}
					</Grid>
					<br/><br/><br/><br/>
					<Tooltip title="Save the Configuration" placement="top">
						<Fab color="primary" className="btn-save-config" aria-label="save" onClick={this.saveConfig.bind(this)}>
							<SaveIcon />
						</Fab>
					</Tooltip>
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
					<HumidityConfigDialog
						open={this.state.dialogOpen}
						onCancel={this.onCancelDialog.bind(this)}
						onSave={this.onSaveDialog.bind(this)}
					>
						<Grid container spacing={3}>
							<Grid item xs={12} sm={4}>
								<InputText 
									name={"curread"}
									id={"cur-read"}
									value={"" + this.state.potConfig.moistureCurRead}
									label={"Current Read"}
									disabled={true}
								/>
							</Grid>
							<Grid item xs={12} sm={4}>
								<InputText 
									name={"minread"}
									id={"min-read"}
									value={"" + this.state.potConfig.minHumidityRead}
									label={"Min Read"}
									disabled={true}
								/>
							</Grid>
							<Grid item xs={12} sm={4}>
								<InputText 
									name={"maxread"}
									id={"max-read"}
									value={"" + this.state.potConfig.maxHumidityRead}
									label={"Max Read"}
									disabled={true}
								/>
							</Grid>
							<Grid item xs={12} sm={6}>
								<InputText 
									name={"wet"}
									id={"wet"}
									value={"" + this.state.potConfig.wetValue}
									label={"Wet Value"}
									onChange={this.onChangeInputDialog.bind(this)}
								/>
							</Grid>
							<Grid item xs={12} sm={6}>
								<InputText 
									name={"dry"}
									id={"dry"}
									value={"" + this.state.potConfig.dryValue}
									label={"Dry Value"}
									onChange={this.onChangeInputDialog.bind(this)}
								/>
							</Grid>
						</Grid>
					</HumidityConfigDialog>
					<PumpCalibrationDialog
						open={this.state.pumpDialogOpen}
						onCancel={this.onCancelPumpDialog.bind(this)}
						onSave={this.onSavePumpDialog.bind(this)}
					>
						<PumpSwitch
							mac={this.state.potConfig.mac || ""}
							id={this.state.potConfig.id || ""}
							status={this.state.potConfig.status || "OFF"}
							onChange={this.onChangePumpInDialog.bind(this)}
						/>
					</PumpCalibrationDialog>
				</React.Fragment>
		);
	}
}

export default App;

ReactDOM.render(<App />, document.getElementById("reactapp"));