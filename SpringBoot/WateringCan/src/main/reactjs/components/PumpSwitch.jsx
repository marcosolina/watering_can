import React, { Component } from 'react';
import PropTypes from 'prop-types';
import Switch from '@material-ui/core/Switch';

/**
 * Switch used to turn on and off the pump
 */
class PumpSwitch extends Component{
	constructor(props){
        super(props);
        this.state = {
            status: this.props.status
        };
    }

    handleChange(event){
        this.setState({status: event.target.checked ? "ON" : "OFF"}, this.callParentOnChange.bind(this));
    };

    componentDidUpdate(prevProps) {
		if (this.props.forceStatus || prevProps.status !== this.props.status && this.props.status !== this.state.status) {
			this.setState({status: this.props.status});
		}
	}

    callParentOnChange(){
        if(this.props.onChange){
            this.props.onChange(this.props.mac, this.props.id, this.state.status);
        }
    }

    render(){
        return(
            <Switch
                checked={this.state.status == "ON"}
                onChange={this.handleChange.bind(this)}
                name="checkedA"
                color="primary"
                inputProps={{ 'aria-label': 'secondary checkbox' }}
            />
        );
    }
}

PumpSwitch.propTypes = {
    mac: PropTypes.string.isRequired,
    id: PropTypes.string.isRequired,
    status: PropTypes.string.isRequired,
    onChange: PropTypes.func.isRequired,
    forceStatus: PropTypes.bool
}

export default PumpSwitch;