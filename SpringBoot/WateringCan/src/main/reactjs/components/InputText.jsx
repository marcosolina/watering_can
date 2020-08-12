import React, { Component } from 'react';
import PropTypes from 'prop-types';
import TextField from '@material-ui/core/TextField';

/**
 * Simple imput text field
 */
class InputText extends Component{
	constructor(props){
        super(props);
        this.state = {
            value: this.props.value || ""
        };
    }

    handleChange(event){
        this.setState({value: event.target.value}, this.callParentOnChange.bind(this));
    };

    componentDidUpdate(prevProps) {
		if (prevProps.value !== this.props.value && this.props.value !== this.state.value) {
			this.setState({value: this.props.value});
		}
	}

    callParentOnChange(){
        if(this.props.onChange){
            this.props.onChange(this.props.id, this.state.value);
        }
    }

    render(){
        return(
            <TextField 
                id={this.props.id}
                label={this.props.label} 
                name={this.props.name}
                disabled={this.props.disabled ? true : false}
                value={this.state.value}
                fullWidth
                onChange={this.handleChange.bind(this)}
            />
            );
    }
}

InputText.propTypes = {
    name: PropTypes.string,
    id: PropTypes.string.isRequired,
    value: PropTypes.string,
    label: PropTypes.string,
    onChange: PropTypes.func,
    disabled: PropTypes.bool
}

export default InputText;