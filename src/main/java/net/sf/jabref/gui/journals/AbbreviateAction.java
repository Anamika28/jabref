/*  Copyright (C) 2003-2014 JabRef contributors.
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/
package net.sf.jabref.gui.journals;

import net.sf.jabref.BibtexEntry;
import net.sf.jabref.Globals;
import net.sf.jabref.BasePanel;
import net.sf.jabref.AbstractWorker;
import net.sf.jabref.gui.undo.NamedCompound;

/**
 * Converts journal full names to either iso or medline abbreviations for all selected entries.
 */
public class AbbreviateAction extends AbstractWorker {

    private final BasePanel panel;
    private String message = "";
    private final boolean iso;


    public AbbreviateAction(BasePanel panel, boolean iso) {
        this.panel = panel;
        this.iso = iso;
    }

    @Override
    public void init() {
        panel.output("Abbreviating...");
    }

    @Override
    public void run() {
        BibtexEntry[] entries = panel.getSelectedEntries();
        if (entries == null) {
            return;
        }

        UndoableAbbreviator undoableAbbreviator = new UndoableAbbreviator(Globals.journalAbbrev, iso);

        NamedCompound ce = new NamedCompound("Abbreviate journal names");
        int count = 0;
        for (BibtexEntry entry : entries) {
            if (undoableAbbreviator.abbreviate(panel.database(), entry, "journal", ce)) {
                count++;
            }
            if (undoableAbbreviator.abbreviate(panel.database(), entry, "journaltitle", ce)) {
                count++;
            }
        }

        if (count > 0) {
            ce.end();
            panel.undoManager.addEdit(ce);
            panel.markBaseChanged();
            message = Globals.lang("Abbreviated %0 journal names.", String.valueOf(count));
        } else {
            message = Globals.lang("No journal names could be abbreviated.");
        }
    }

    @Override
    public void update() {
        panel.output(message);
    }
}