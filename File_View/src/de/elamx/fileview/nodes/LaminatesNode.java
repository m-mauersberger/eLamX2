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
package de.elamx.fileview.nodes;

import de.elamx.fileview.nodefactories.LaminatesNodeFactory;
import java.awt.Image;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Andreas Hauffe
 */
public class LaminatesNode extends AbstractNode {

    @SuppressWarnings("this-escape")
    public LaminatesNode() {
        super(Children.create(new LaminatesNodeFactory(), true));
        this.setName(NbBundle.getMessage(LaminatesNode.class,"LaminatesNode.name"));
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("de/elamx/fileview/resources/laminates.png");
    }

    @Override
    public Image getOpenedIcon(int i) {
        return getIcon(i);
    }

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> laminateActions = Utilities.actionsForPath("eLamXActions/Laminates");
        return laminateActions.toArray(new Action[laminateActions.size()]);
    }
}
