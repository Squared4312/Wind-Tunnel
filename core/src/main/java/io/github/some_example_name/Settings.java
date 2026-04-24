package io.github.some_example_name;

public class Settings {
    private float flowSpeed = 0.100f;
    private float viscosity = 0.020f;
    private String plot;
    private String mode;
    private boolean showFlowLines = false;

    private String[] plotValues = {"density", "x velocity", "y velocity", "speed", "curl"};
    private String[] modeValues = {"draw barriers", "erase barriers", "drag fluid"};

    private boolean simulationRunning = false;

    public Settings() {
        this.plot = this.plotValues[1];
        this.mode = this.modeValues[1];
    }

    public void resetSettings() {
        this.flowSpeed = 0.100f;
        this.viscosity = 0.020f;
        this.plot = this.plotValues[1];
        this.mode = this.modeValues[0];
        this.showFlowLines = false;
    }

    public String plotOrModeButtonClicked(Integer change, String currentValue, String[] possibleValues) {
        Integer index = 0;
        for (Integer count=0; count<possibleValues.length; count++) {
            if (possibleValues[count] == currentValue) {
                index = count;
                break;
            }
        }
        if (change == 1) {
            if (index == possibleValues.length-1) {
                return possibleValues[0];
            }
            return possibleValues[index+1];
        } else if (change == -1) {
            if (index == 0) {
                return possibleValues[possibleValues.length-1];
            }
            return possibleValues[index-1];
        }
        return currentValue;
    }

    public boolean getSimulationRunning() {return this.simulationRunning;}
    public void setSimulationRunning(boolean simulationRunning) {this.simulationRunning = simulationRunning;}

    public float getFlowSpeed() {return this.flowSpeed;}
    public void setFlowSpeed(float flowSpeed) {this.flowSpeed = flowSpeed;}

    public float getViscosity() {return this.viscosity;}
    public void setViscosity(float viscosity) {this.viscosity = viscosity;}

    public String getPlot() {return this.plot;}
    public void setPlot(String plot) {this.plot = plot;}

    public String getMode() {return this.mode;}
    public void setMode(String mode) {this.mode = mode;}

    public boolean getShowFlowLines() {return this.showFlowLines;}
    public void setShowFlowLines(boolean showFlowLines) {this.showFlowLines = showFlowLines;}

    public String[] getPlotValues() {return this.plotValues;}
    public void setPlotValues(String[] plotValues) {this.plotValues = plotValues;}

    public String[] getModeValues() {return this.modeValues;}
    public void setModeValues(String[] modeValues) {this.modeValues = modeValues;}
}
