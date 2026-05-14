package io.github.some_example_name;

import com.badlogic.gdx.math.Vector2;

public class Settings {

    private Vector2 resolution;
    private String solver;
    private float flowSpeed;
    private float viscosity;
    private String plot;
    private String mode;
    private boolean showFlowLines;

    private String[] plotValues = {"density", "x velocity", "y velocity", "speed", "curl"};
    private String[] modeValues = {"draw barriers", "erase barriers", "drag fluid"};
    private Integer[][] resolutionValues = {{32, 18}, {64, 36}, {128, 72}, {256, 144}, {512, 288}, {1024, 576}}; // keep the 16:9 aspect ratio
    private String[] solverValues = {"2D LBM", "3D LBM"};

    private boolean simulationRunning = false;

    private static Settings instance;

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    private Settings() {
        reset();
    }

    public void reset() {
        this.resolution = new Vector2(this.resolutionValues[3][0], this.resolutionValues[3][1]);
        this.solver = this.solverValues[0];
        this.flowSpeed = 0.100f;
        this.viscosity = 0.020f;
        this.plot = this.plotValues[1];
        this.mode = this.modeValues[0];
        this.showFlowLines = false;
    }

    public String cycleOptions(Integer change, String currentValue, String[] possibleValues) {
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

    public void cycleResolutionOptions(Integer change) {
        Integer index = 0;
        for (Integer count=0; count<resolutionValues.length; count++) {
            if (resolutionValues[count][0] == this.resolution.x && resolutionValues[count][1] == this.resolution.y) {
                index = count;
                break;
            }
        }
        if (change == 1) {
            if (index == resolutionValues.length-1) {
                this.resolution = new Vector2(resolutionValues[0][0], resolutionValues[0][1]);
            } else {
                this.resolution = new Vector2(resolutionValues[index+1][0], resolutionValues[index+1][1]);
            }
        } else if (change == -1) {
            if (index == 0) {
                this.resolution = new Vector2(resolutionValues[resolutionValues.length-1][0], resolutionValues[resolutionValues.length-1][1]);
            } else {
                this.resolution = new Vector2(resolutionValues[index-1][0], resolutionValues[index-1][1]);
            }
        }
    }

    public boolean getSimulationRunning() {return this.simulationRunning;}
    public void setSimulationRunning(boolean simulationRunning) {this.simulationRunning = simulationRunning;}

    public String getSolver() {return this.solver;}
    public void setSolver(String solver) {this.solver = solver;}

    public Vector2 getResolution() {return this.resolution;}
    public void setResolution(Vector2 resolution) {this.resolution = resolution;}

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

    public Integer[][] getResolutionValues() {return this.resolutionValues;}
    public void setResolutionValues(Integer[][] resolutionValues) {this.resolutionValues = resolutionValues;}

    public String[] getSolverValues() {return this.solverValues;}
    public void setSolverValues(String[] solverValues) {this.solverValues = solverValues;}
}
