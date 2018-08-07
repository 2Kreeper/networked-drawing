package eu.barononline.networked_drawing.ui;

import com.sun.istack.internal.NotNull;
import eu.barononline.networked_drawing.networking.NetworkConnection;
import eu.barononline.networked_drawing.ui.interaction.UserInteractionHandler;
import eu.barononline.networked_drawing.ui.shapes.Shapes;
import eu.barononline.networked_drawing.util.ColorUtil;

import javax.swing.*;
import java.awt.*;

public class DrawFrame extends JFrame {

    private NetworkConnection conn;
    private DrawCanvas canvas;
    private JToolBar toolBar;

    public DrawFrame(@NotNull String title, int width, int height, @NotNull NetworkConnection conn) {
        setVisible(false);

        setTitle(title);
        setSize(width, height);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(new JPanel(new BorderLayout()));

        this.conn = conn;

        initComponents();

        setVisible(true);

        canvas.requestFocus();
    }

    private void initComponents() {
        canvas = new DrawCanvas(conn);
        conn.registerDrawReceiver(canvas);
        conn.registerDeleteReceiver(canvas);
        conn.registerUndoReceiver(canvas);
        conn.registerRedoReceiver(canvas);
        getContentPane().add(canvas, BorderLayout.CENTER);

        UserInteractionHandler interactionHandler = canvas.getHandler();

        toolBar = new JToolBar("Toolbar");
        ButtonGroup shapeGroup = new ButtonGroup();

        JToggleButton circleOption = new JToggleButton("Oval");
        circleOption.addActionListener((e) -> canvas.getHandler().setShape(Shapes.OVAL));
        circleOption.doClick();
        shapeGroup.add(circleOption);
        toolBar.add(circleOption);

        JToggleButton rectOption = new JToggleButton("Rectangle");
        rectOption.addActionListener((e) -> canvas.getHandler().setShape(Shapes.RECTANGLE));
        rectOption.setSelected(true);
        shapeGroup.add(rectOption);
        toolBar.add(rectOption);

        toolBar.addSeparator();

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener((e) -> canvas.getHandler().onDeletePressed());
        toolBar.add(deleteButton);

        toolBar.addSeparator();

        JButton colorSelectionButton = new JButton("Color");
        colorSelectionButton.setBackground(interactionHandler.getDrawColor());
        colorSelectionButton.setForeground(ColorUtil.complementaryColor(interactionHandler.getDrawColor()));
        colorSelectionButton.addActionListener((e) -> {
            Color c = JColorChooser.showDialog(colorSelectionButton, "Select a draw color", interactionHandler.getDrawColor());
            if(c == null) {
                return;
            }

            interactionHandler.setDrawColor(c);
            colorSelectionButton.setBackground(c);
            colorSelectionButton.setForeground(ColorUtil.complementaryColor(c));
        });
        toolBar.add(colorSelectionButton);

        getContentPane().add(toolBar, BorderLayout.NORTH);
    }
}
