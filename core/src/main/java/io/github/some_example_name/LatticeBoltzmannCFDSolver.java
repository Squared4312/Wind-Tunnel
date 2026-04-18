package io.github.some_example_name;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class LatticeBoltzmannCFDSolver {
    private MenuUtil util;
    private Settings settings;

    private double weightO = 4.0/9.0; // Origin
    private double weightAxis = 1.0/9.0; // N E S W
    private double weightDiagonals = 1.0/36.0; // NE NW SE SW

    // setup mesh
    private Cell[][] cells;
    private Vector2 numberOfCells = new Vector2(32, 18); // keep the 16:9 aspect ratio
    private Vector2 cellDimensions;

    public LatticeBoltzmannCFDSolver() {
        this.util = new MenuUtil();
        this.settings = new Settings();

        this.cellDimensions = new Vector2(util.getScreenDimensions().x/numberOfCells.x, util.getScreenDimensions().y/numberOfCells.y);
        this.cells = new Cell[(int) numberOfCells.x][(int) numberOfCells.y];
        for (int column=0; column<numberOfCells.x; column++) {
            for (int row=0; row<numberOfCells.y; row++) {
                Vector2 centre = new Vector2(column*cellDimensions.x+(cellDimensions.x/2), row*cellDimensions.y+(cellDimensions.y/2));
                Vector2 dimensions = new Vector2(cellDimensions.x, cellDimensions.y);
                this.cells[column][row] = new Cell(centre, dimensions, settings.getFlowSpeed());
            }
        }
    }

    public void render(ShapeRenderer sr) {
        for (Integer column=0; column<cells.length; column++) {
            for (Integer row=0; row<cells[column].length; row++) {
                cells[column][row].draw(sr, util.getScreenDimensions(), settings.getShowFlowLines());
            }
        }
    }

    public void collision() {
        double omega = 1/((3*settings.getViscosity())+0.5);
        for (Integer column=0; column<numberOfCells.x; column++) {
            for (Integer row=0; row<numberOfCells.y; row++) {
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
        // create a new list and calculate the new values to the new list, then write it back to the cells

        double[][][] newCellValues = new double[(int) numberOfCells.x][(int) numberOfCells.y][9];
        // 0: rest, 1:N, 2:E, 3:S, 4:W, 5:NE, 6:NW, 7:SE, 8: SW

        for (Integer column=0; column<newCellValues.length; column++) {
            for (Integer row=0; row<newCellValues[column].length; row++) {
                if (row+1 < numberOfCells.y) {newCellValues[column][row][1] = cells[column][row+1].getDensityN();}
                if (column-1 >= 0) {newCellValues[column][row][2] = cells[column-1][row].getDensityE();}
                if (row-1 >= 0) {newCellValues[column][row][3] = cells[column][row-1].getDensityS();}
                if (column+1 < numberOfCells.x) {newCellValues[column][row][4] = cells[column+1][row].getDensityW();}

                if (row+1 < numberOfCells.y && column-1 >= 0) {newCellValues[column][row][5] = cells[column-1][row+1].getDensityNE();}
                if (row+1 < numberOfCells.y && column+1 < numberOfCells.x) {newCellValues[column][row][6] = cells[column+1][row+1].getDensityNW();}
                if (row-1 >= 0 && column-1 >= 0) {newCellValues[column][row][7] = cells[column-1][row-1].getDensitySE();}
                if (row-1 >= 0 && column+1 < numberOfCells.x) {newCellValues[column][row][8] = cells[column+1][row-1].getDensitySW();}
            }
        }
        for (Integer column=0; column<newCellValues.length; column++) {
            for (Integer row = 0; row<newCellValues[column].length; row++) {
                cells[column][row].setDensityN(newCellValues[column][row][1]);
                cells[column][row].setDensityE(newCellValues[column][row][2]);
                cells[column][row].setDensityS(newCellValues[column][row][3]);
                cells[column][row].setDensityW(newCellValues[column][row][4]);
                cells[column][row].setDensityNE(newCellValues[column][row][5]);
                cells[column][row].setDensityNW(newCellValues[column][row][6]);
                cells[column][row].setDensitySE(newCellValues[column][row][7]);
                cells[column][row].setDensitySW(newCellValues[column][row][8]);
            }
        }
    }

    public void boundaries() {
        for (Integer column=0; column<numberOfCells.x; column++) {
            for (Integer row=0; row<numberOfCells.y; row++) {
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

