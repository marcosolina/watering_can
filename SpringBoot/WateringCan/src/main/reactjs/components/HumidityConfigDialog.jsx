import React, { Component } from 'react';
import PropTypes from 'prop-types';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import Grid from '@material-ui/core/Grid';
import InputText from './InputText.jsx';
import Button from '@material-ui/core/Button';

class HumidityConfigDialog extends Component{
	constructor(props){
        super(props);
        this.state = {
        };
    }

    render(){
        return(
            <Dialog open={this.props.open} aria-labelledby="form-dialog-title">
                <DialogTitle id="form-dialog-title">Set the min and max values</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Set the value for the "Wet" status and "Dry" status
                    </DialogContentText>
                    {this.props.children}
                </DialogContent>
                <DialogActions>
                    <Button onClick={this.props.onCancel} color="primary">
                        Cancel
                    </Button>
                    <Button onClick={this.props.onSave} color="primary">
                        Save
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