/*
 *  This program developed in Java is based on the netbeans platform and is used
 *  to design and to analyse composite structures by means of analytical and 
 *  numerical methods.
 * 
 *  Further information can be found here:
 *  http://www.elamx.de
 *    
 *  Copyright (C) 2021 Technische Universität Dresden - Andreas Hauffe
 * 
 *  This file is part of eLamX².
 *
 *  eLamX² is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  eLamX² is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with eLamX².  If not, see <http://www.gnu.org/licenses/>.
 */
package de.elamx.clt.plateui.buckling.batchrun;

import ch.systemsx.cisd.hdf5.HDF5CompoundType;
import ch.systemsx.cisd.hdf5.IHDF5Writer;
import de.elamx.clt.CLT_Laminate;
import de.elamx.clt.plate.BucklingResult;
import de.elamx.clt.plateui.buckling.BucklingModuleData;
import de.elamx.clt.plateui.buckling.InputPanel;
import de.elamx.laminate.Laminat;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Florian Dexl
 */
@ServiceProvider(service = HDF5BucklingOutputWriterService.class, position = 1)
public class HDF5BucklingOutputWriterServiceImpl implements HDF5BucklingOutputWriterService {

    private static HDF5CompoundType<List<?>> HDF5criticalLoadsType = null;
    private static HDF5CompoundType<List<?>> HDF5plateGeometryType = null;
    private static HDF5CompoundType<List<?>> HDF5termNumberType = null;

    @Override
    public void writeResults(IHDF5Writer hdf5writer, BucklingModuleData data, Laminat laminate, BucklingResult result) {
        String bucklingGroup = "laminates/".concat(data.getLaminat().getName().concat("/buckling/"));
        if (!hdf5writer.object().exists(bucklingGroup)) {
            hdf5writer.object().createGroup(bucklingGroup);
        }
        String groupName = bucklingGroup.concat(data.getName());
        hdf5writer.object().createGroup(groupName);
        
        String inputGroup = groupName.concat("/input data/");

        hdf5writer.object().createGroup(inputGroup);

        ArrayList<Double> plateGeometryValuesArrayList = new ArrayList<>();
        ArrayList<String> plateGeometryNamesArrayList = new ArrayList<>();

        plateGeometryValuesArrayList.add(data.getBucklingInput().getLength());
        plateGeometryNamesArrayList.add("length");

        plateGeometryValuesArrayList.add(data.getBucklingInput().getWidth());
        plateGeometryNamesArrayList.add("width");

        if (HDF5plateGeometryType == null) {
            HDF5plateGeometryType = hdf5writer.compound().getInferredType("Plate geometry", plateGeometryNamesArrayList, plateGeometryValuesArrayList);
        }
        
        hdf5writer.compound().write(inputGroup.concat("/plate geometry"), HDF5plateGeometryType, plateGeometryValuesArrayList);

        hdf5writer.string().write(inputGroup.concat("/BCx"), InputPanel.getBoundaryConditionString(data.getBucklingInput().getBcx()));
        hdf5writer.string().write(inputGroup.concat("/BCy"), InputPanel.getBoundaryConditionString(data.getBucklingInput().getBcy()));

        ArrayList<Integer> termNumberValuesArrayList = new ArrayList<>();
        ArrayList<String> termNumberNamesArrayList = new ArrayList<>();

        termNumberValuesArrayList.add(data.getBucklingInput().getM());
        termNumberNamesArrayList.add("m (x)");

        termNumberValuesArrayList.add(data.getBucklingInput().getN());
        termNumberNamesArrayList.add("n (y)");

        if (HDF5termNumberType == null) {
            HDF5termNumberType = hdf5writer.compound().getInferredType("Term number", termNumberNamesArrayList, termNumberValuesArrayList);
        }
        
        hdf5writer.compound().write(inputGroup.concat("/term number"), HDF5termNumberType, termNumberValuesArrayList);        

        double[][] dmat = data.getBucklingInput().getDMatrixService().getDMatrix(data.getLaminat().getLookup().lookup(CLT_Laminate.class));
        String dMatrixOption = data.getBucklingInput().getDMatrixService().getBatchRunOutput();

        hdf5writer.float64().createMatrix(groupName.concat("/D matrix used"), 3, 3);
        hdf5writer.float64().writeMatrix(groupName.concat("/D matrix used"), dmat);
        hdf5writer.string().setAttr(groupName.concat("/D matrix used"), "D matrix option", dMatrixOption);

        if (data.getAlphaBar() != null) {
            hdf5writer.float64().write(groupName.concat("/effective aspect ratio"), data.getAlphaBar());
        }

        double[] ncrit = result.getN_crit();
        ArrayList<Double> criticalLoadValuesArrayList = new ArrayList<>();
        ArrayList<String> criticalLoadNamesArrayList = new ArrayList<>();

        criticalLoadValuesArrayList.add(ncrit[0]);
        criticalLoadNamesArrayList.add("nx_crit");

        criticalLoadValuesArrayList.add(ncrit[1]);
        criticalLoadNamesArrayList.add("ny_crit");

        criticalLoadValuesArrayList.add(ncrit[2]);
        criticalLoadNamesArrayList.add("nxy_crit");

        if (HDF5criticalLoadsType == null) {
            HDF5criticalLoadsType = hdf5writer.compound().getInferredType("Critical loads", criticalLoadNamesArrayList, criticalLoadValuesArrayList);
        }

        hdf5writer.compound().write(groupName.concat("/critical load"), HDF5criticalLoadsType, criticalLoadValuesArrayList);

        double[] eigenvalues = result.getEigenvalues_();
        int numberOfEigenvalues = eigenvalues.length;
        hdf5writer.float64().createArray(groupName.concat("/eigenvalues"), numberOfEigenvalues);
        hdf5writer.float64().writeArray(groupName.concat("/eigenvalues"), eigenvalues);
        hdf5writer.int32().setAttr(groupName.concat("/eigenvalues"), "number of eigenvalues", numberOfEigenvalues);
    }
}
