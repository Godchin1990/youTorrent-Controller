/*******************************************************************************
 * Copyright (c) 2014 Luis M. Gallardo D..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Luis M. Gallardo D.
 ******************************************************************************/
package com.lgallardo.youtorrentcontroller;

import java.util.Comparator;

public class TorrentProgressComparator implements Comparator<Torrent> {

    boolean reversed = false;

    TorrentProgressComparator(boolean reversed) {
        this.reversed = reversed;
    }

    @Override
    public int compare(Torrent t1, Torrent t2) {

        String percentage1 = t1.getPercentage();
        String percentage2 = t2.getPercentage();

        int p1 = Integer.parseInt(percentage1);
        int p2 = Integer.parseInt(percentage2);

        if (reversed) {
            // Ascending order
            return p1 - p2;
        } else {
            // Descending order
            return p2 - p1;
        }
    }
}

