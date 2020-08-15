import React, { Component } from 'react';
import PropTypes from 'prop-types';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import Button from '@material-ui/core/Button';

/**
 * Simple dialog view used to calibrate the moisture sensor
 */
class HumidityConfigDialog extends Component{
	constructor(props){
        super(props);
        this.state = {
        };
    }

    render(){
        return(
            <Dialog open={this.props.open} aria-labelledby="form-dialog-title">
                <DialogTitle id="form-dialog-title">Set the "Dry" and "Wet" values</DialogTitle>
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

HumidityConfigDialog.propTypes = {
    open: PropTypes.bool,
    onCancel: PropTypes.func.isRequired,
    onSave: PropTypes.func.isRequired
}

export default HumidityConfigDialog;