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

class App extends Component{
	constructor(props) {
		super(props);
		this.state = {
			pots: {},
			snackOpen: false,
			snackMessage: "",
			firstLoad: true
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
				const p = {...this.state.pots};
				for(let i = 0; i < resp.pots.length; i++){
					p[resp.pots[i].mac + "_" + resp.pots[i].id].status = resp.pots[i].status;
				}
				this.setState({pots: p});
			}
			setTimeout(this.retrieveListOfPots.bind(this), 2000);
        }
	}


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
	
	onChangeInput(id, newValue){
		let pots = this.state.pots;

		let potKey = id;
		pots[potKey].description = newValue;

		this.setState({pots: pots});
	}

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

	onChangeSlider(mac, id, value){
		let pots = this.state.pots;

		let potKey = mac + "_" + id;
		pots[potKey].ml = value;
		pots[potKey].forceStatus = false;

		this.setState({pots});
	}


	render(){

		let arrPots = [];
		for (const property in this.state.pots) {
			arrPots.push(this.state.pots[property]);
		}

		return(
				<Grid container spacing={3}>
					{
						arrPots.map((pot, index) => {
							return  <Grid item xs={12} sm={6} md={4} lg={3} xl={2} key={index}>
										<Card>
											<CardContent>
												<InputText 
													name={"" + pot.mac + "_" + pot.id}
													id={"" + pot.mac + "_" + pot.id}
													value={pot.description}
													label={"Plant"}
													onChange={this.onChangeInput.bind(this)}
												/>
												<br/><br/>
												<MlSlider 
													mac={pot.mac}
													id={pot.id}
													value={pot.ml}
													onChange={this.onChangeSlider.bind(this)}
												/>
											</CardContent>
											<CardActions>
												<PumpSwitch
													mac={pot.mac}
													id={pot.id}
													status={pot.status}
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