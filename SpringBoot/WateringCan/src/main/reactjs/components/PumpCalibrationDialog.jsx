import React, { Component } from 'react';
import PropTypes from 'prop-types';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';

/**
 * Simple dialog view used to calibrate the moisture sensor
 */
class PumpCalibrationDialog extends Component{
	constructor(props){
        super(props);
        this.state = {
        };
    }

    render(){
        return(
            <Dialog open={this.props.open} aria-labelledby="form-dialog-title">
                <DialogTitle id="form-dialog-title">
                    <Typography variant="body2">
                        To calibrate your pump you need a measuring cup <img className="measuring-cup" src="/WateringCan/pictures/measuring_cup.png"/>
                         and let the pump to pour the water into the cup. Start the pump and then stop it when it has poured 1000ml (One liter) of water.
                        That's it. Don't forget to save the configuration from the main screen
                    </Typography>
                </DialogTitle>
                <DialogContent>
                    {this.props.children}
                </DialogContent>
                <DialogActions>
                    <Button onClick={this.props.onCancel} color="primary">
                        Cancel
                    </Button>
                    <Button onClick={this.props.onSave} color="primary">
                        Ok
                    </Button>
                </DialogActions>
            </Dialog>
            );
    }
}

PumpCalibrationDialog.propTypes = {
    open: PropTypes.bool,
    onCancel: PropTypes.func.isRequired,
    onSave: PropTypes.func.isRequired
}

export default PumpCalibrationDialog;