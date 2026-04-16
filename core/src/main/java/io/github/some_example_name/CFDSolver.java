package io.github.some_example_name;

import com.badlogic.gdx.math.Vector2;

public class CFDSolver {
    private MenuUtil util;
    private SettingsMenu settings;

    private double weightO = 4.0/9.0; // Origin
    private double weightAxis = 1.0/9.0; // N E S W
    private double weightDiagonals = 1.0/36.0; // NE NW SE SW

    // setup mesh
    private Cell[][] cells;
    private Vector2 numberOfCells = new Vector2(32, 18); // keep the 16:9 aspect ratio
    private Vector2 cellDimensions = new Vector2(util.getScreenDimensions().x/numberOfCells.x, util.getScreenDimensions().y/numberOfCells.y);

    public CFDSolver() {
        this.util = new MenuUtil();
        this.settings = new SettingsMenu();

        this.cells = new Cell[(int) numberOfCells.x][(int) numberOfCells.y];
        for (int column=0; column<numberOfCells.x; column++) {
            for (int row=0; row<numberOfCells.y; row++) {
                Vector2 centre = new Vector2(column*cellDimensions.x+(cellDimensions.x/2), row*cellDimensions.y+(cellDimensions.y/2));
                Vector2 dimensions = new Vector2(cellDimensions.x, cellDimensions.y);
                this.cells[column][row] = new Cell(centre, dimensions, settings.getFlowSpeed(), weightO, weightAxis, weightDiagonals);
            }
        }
    }

    public void collision() {
        double omega = 1/((3*settings.getViscosity())+0.5);
        for (int column=0; column<numberOfCells.x; column++) {
            for (int row=0; row<numberOfCells.y; row++) {
                Cell cell = cells[column][row];
                if (!cell.getIsBarrier()) {
                    // calculate density
                    double densityTotal = cell.getDensityO()+cell.getDensityN()+cell.getDensityE()+cell.getDensityS()+cell.getDensityW()+cell.getDensityNE()+cell.getDensityNW()+cell.getDensitySE()+cell.getDensitySW();
                    // calculate velocity
                    double vx = (cell.getDensityE()+cell.getDensityNE()+cell.getDensitySE()-cell.getDensityW()-cell.getDensityNW()-cell.getDensitySW())/densityTotal;
                    double vy = (cell.getDensityN()+cell.getDensityNE()+cell.getDensityNW()-cell.getDensityS()-cell.getDensitySE()-cell.getDensitySW())/densityTotal;
                    double v2 = vx*vx + vy*vy;
                    // update each direction towards equilibrium
                    cell.setDensityO(cell.getDensityO()+omega*((weightO*densityTotal)*(1-(1.5*v2))-cell.getDensityO()));

                    cell.setDensityN(cell.getDensityN()+omega*((weightAxis*densityTotal)*(1+(3*vy)+(4.5*vy*vy)-(1.5*v2))-cell.getDensityN()));
                    cell.setDensityE(cell.getDensityE()+omega*((weightAxis*densityTotal)*(1+(3*vx)+(4.5*vx*vx)-(1.5*v2))-cell.getDensityE()));
                    cell.setDensityS(cell.getDensityS()+omega*((weightAxis*densityTotal)*(1+(3*-vy)+(4.5*vy*vy)-(1.5*v2))-cell.getDensityS()));
                    cell.setDensityW(cell.getDensityW()+omega*((weightAxis*densityTotal)*(1+(3*-vx)+(4.5*vx*vx)-(1.5*v2))-cell.getDensityW()));

                    cell.setDensityNE(cell.getDensityNE()+omega*((weightDiagonals*densityTotal)*(1+(3*vx)+(3*vy)+(4.5*(v2+(2*vx*vy)))-(1.5*v2))-cell.getDensityNE()));
                    cell.setDensityNW(cell.getDensityNW()+omega*((weightDiagonals*densityTotal)*(1+(3*-vx)+(3*vy)+(4.5*(v2+(2*-vx*vy)))-(1.5*v2))-cell.getDensityNW()));
                    cell.setDensitySE(cell.getDensitySE()+omega*((weightDiagonals*densityTotal)*(1+(3*vx)+(3*-vy)+(4.5*(v2+(2*vx*-vy)))-(1.5*v2))-cell.getDensitySE()));
                    cell.setDensitySW(cell.getDensitySW()+omega*((weightDiagonals*densityTotal)*(1+(3*-vx)+(3*-vy)+(4.5*(v2+(2*-vx*-vy)))-(1.5*v2))-cell.getDensitySW()));
                }
            }
        }
    }

    public void movement() {

        // create a new list and calculate the new values to the new list, then write it to the cells

        // if (row-1 >= 0 && column+1 < numberOfCells.x-1) {cells[column+1][row-1].setDensityNE(cells[column][row].getDensityNE());}
        // if (row-1 >= 0 && column-1 >= 0) {cells[column-1][row-1].setDensityNW(cells[column][row].getDensityNW());}
        // if (row+1 < numberOfCells.y-1 && column+1 < numberOfCells.x-1) {cells[column+1][row+1].setDensitySE(cells[column][row].getDensitySE());}
        // if (row+1 < numberOfCells.y-1 && column-1 >= 0) {cells[column-1][row+1].setDensitySW(cells[column][row].getDensitySW());}

        // movement
        for (int column=0; column<numberOfCells.x; column++) { // left to right
            for (int row=0; row<numberOfCells.y; row++) { // top to bottom (take the down densityN)
                if (row+1 < numberOfCells.y-1) {cells[column][row].setDensityN(cells[column][row+1].getDensityN());}
            }
            for (int row = (int) (numberOfCells.y-1); row>=0; row--) { // bottom to top (take the up densityS)
                if (row-1 >= 0) {cells[column][row].setDensityS(cells[column][row-1].getDensityS());}
            }
        }
        for (int row=0; row<numberOfCells.y; row++) { // top to bottom
            for (int column = (int) (numberOfCells.x-1); column>=0; column--) { // right to left (take the left densityE)
                if (column-1 >= 0) {cells[column][row].setDensityE(cells[column-1][row].getDensityE());}
            }
            for (int column=0; column<numberOfCells.x; column++) { // left to right (take the right densityW)
                if (column+1 < numberOfCells.x-1) {cells[column][row].setDensityW(cells[column+1][row].getDensityW());}
            }
        }
    }

    public void boundaries() {
        for (int column=0; column<numberOfCells.x; column++) {
            for (int row=0; row<numberOfCells.y; row++) {
                if (cells[column][row].getIsBarrier()) {
                    // reverse all directions
                    if (row+1 < numberOfCells.y-1) {cells[column][row+1].setDensityS(cells[column][row].getDensityN());}
                    if (column-1 >= 0) {cells[column-1][row].setDensityW(cells[column][row].getDensityE());}
                    if (row-1 >= 0) {cells[column][row-1].setDensityN(cells[column][row].getDensityS());}
                    if (column+1 < numberOfCells.x-1) {cells[column+1][row].setDensityE(cells[column][row].getDensityW());}

                    if (column-1 >= 0 && row+1 < numberOfCells.y-1) {cells[column-1][row+1].setDensitySW(cells[column][row].getDensityNE());}
                    if (column+1 < numberOfCells.x-1 && row+1 < numberOfCells.y-1) {cells[column+1][row+1].setDensitySE(cells[column][row].getDensityNW());}
                    if (column-1 >= 0 && row-1 >= 0) {cells[column-1][row-1].setDensityNW(cells[column][row].getDensitySE());}
                    if (column+1 < numberOfCells.x-1 && row-1 >= 0) {cells[column+1][row-1].setDensityNE(cells[column][row].getDensitySW());}

                    cells[column][row].setDensityN(0);
                    cells[column][row].setDensityE(0);
                    cells[column][row].setDensityS(0);
                    cells[column][row].setDensityW(0);

                    cells[column][row].setDensityNE(0);
                    cells[column][row].setDensityNW(0);
                    cells[column][row].setDensitySE(0);
                    cells[column][row].setDensitySW(0);
                }

                // set the left-most cells to be the flow speed to constantly add new fluid
                cells[0][row].setDensityE(weightAxis*(1+(3*settings.getFlowSpeed())+(3*settings.getFlowSpeed()*settings.getFlowSpeed())));
                cells[0][row].setDensityNE(weightDiagonals*(1+(3*settings.getFlowSpeed())+(3*settings.getFlowSpeed()*settings.getFlowSpeed())));
                cells[0][row].setDensitySE(weightDiagonals*(1+(3*settings.getFlowSpeed())+(3*settings.getFlowSpeed()*settings.getFlowSpeed())));
            }
        }
    }
}

