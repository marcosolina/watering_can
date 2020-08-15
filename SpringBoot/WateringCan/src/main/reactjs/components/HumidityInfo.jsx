import React, { Component } from 'react';
import PropTypes from 'prop-types';
import Avatar from '@material-ui/core/Avatar';
import Chip from '@material-ui/core/Chip';

class HumidtyInfo extends Component{
	constructor(props){
        super(props);
        this.state = {
            value: this.props.value || ""
        };
    }

    componentDidUpdate(prevProps) {
		if (prevProps.value !== this.props.value && this.props.value !== this.state.value) {
			this.setState({value: this.props.value});
		}
	}

    render(){
        return(
                <Chip color="primary" size="small" avatar={<Avatar>{this.state.value}</Avatar>} label="% Soil Humidity"/>
            );
    }
}

HumidtyInfo.propTypes = {
    value: PropTypes.number.isRequired
}

export default HumidtyInfo;