import React, { Component } from 'react';
import PropTypes from 'prop-types';
import Switch from '@material-ui/core/Switch';

class Pump extends Component{
	constructor(props){
        super(props);
        this.state = {
            status: this.props.status
        };
    }

    handleChange(event){
        this.setState({status: event.target.checked ? "ON" : "OFF"}, this.callParentOnChange.bind(this));
    };

    callParentOnChange(){
        if(this.props.onChange){
            this.props.onChange(this.props.ip, this.props.id, this.state.status);
        }
    }

    render(){
        return(
        <div>
            <Switch
                checked={this.state.status == "ON"}
                onChange={this.handleChange.bind(this)}
                name="checkedA"
                color="primary"
                inputProps={{ 'aria-label': 'secondary checkbox' }}
            />
        </div>);
    }
}

Pump.propTypes = {
    ip: PropTypes.string.isRequired,
    id: PropTypes.string.isRequired,
    status: PropTypes.string.isRequired,
	onChange: PropTypes.func.isRequired
}

export default Pump;