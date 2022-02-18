package net.flamgop;

import net.flamgop.gui.MainWindow;
import net.minestom.server.command.builder.Command;

public class GetSplineSetsCommand extends Command {
    public GetSplineSetsCommand() {
        super("getSplineSets");

        addSyntax((sender, context) -> {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < MainWindow.xSetContinental.length; i++) {
                if (i != MainWindow.xSetContinental.length-1) {
                    builder.append(MainWindow.xSetContinental[i]).append(", ");
                } else {
                    builder.append(MainWindow.xSetContinental[i]);
                }
            }
            sender.sendMessage("xSet: " + builder.toString());
            builder = new StringBuilder();
            for (int i = 0; i < MainWindow.ySetContinental.length; i++) {
                if (i != MainWindow.ySetContinental.length-1) {
                    builder.append(MainWindow.ySetContinental[i]).append(", ");
                } else {
                    builder.append(MainWindow.ySetContinental[i]);
                }
            }
            sender.sendMessage("ySet: " + builder.toString());
        });
    }
}
