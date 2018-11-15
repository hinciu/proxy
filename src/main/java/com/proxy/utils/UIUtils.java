package com.proxy.utils;

import javax.swing.*;
import java.util.EnumSet;

public class UIUtils {
    public static void setLeadStates(JComboBox box) {
        LeadState[] states = LeadState.values();

        for (int i = 0; i <= states.length - 1; i++) {
            box.addItem(states[i].toString());
        }
    }
}
