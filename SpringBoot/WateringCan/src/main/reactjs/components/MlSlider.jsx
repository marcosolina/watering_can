import React, { Component } from 'react';
import PropTypes from 'prop-types';
import Slider from '@material-ui/core/Slider';
import Typography from '@material-ui/core/Typography';

/**
 * Slider used to specifiy how many ml to pour into the pot
 */
class MlSlider extends Component{
	constructor(props){
        super(props);
        this.state = {
            ml: this.props.value || 0
        };
    }

    handleChange(event, newValue){
        this.setState({ml: newValue}, this.callParentOnChange.bind(this));
    };

    componentDidUpdate(prevProps) {
		if (prevProps.value !== this.props.value && this.props.value !== this.state.ml) {
			this.setState({value: this.props.value});
        }
	}

    callParentOnChange(){
        if(this.props.onChange){
            this.props.onChange(this.props.mac, this.props.id, this.state.ml);
        }
    }

    valueText(value){
        return value + " ml";
    }
    
    render(){
		const step = 100;
		const min = 0;
		const max = 1000;
        const marks = [];
        for(let i = 0; i <= max; i = i + step ){
            marks.push({
                value: i,
                label: i + ""
            });
        }
        return(
            <div>   
                <Typography id={this.props.mac + "_" + this.props.id} gutterBottom>
                    Milliliters to pour
                </Typography>
                <Slider 
                    defaultValue={this.state.ml}
                    value={this.state.ml}
                    getAriaValueText={this.valueText.bind(this)}
                    onChange={this.handleChange.bind(this)}
                    aria-labelledby={this.props.mac + "_" + this.props.id}
                    step={step}
					max={max}
					min={min}
                    valueLabelDisplay="auto"
                    marks={marks}
                />
            </div>
        );
    }
}

MlSlider.propTypes = {
    mac: PropTypes.string.isRequired,
    id: PropTypes.string.isRequired,
    value: PropTypes.number,
	onChange: PropTypes.func.isRequired
}

export default MlSlider;