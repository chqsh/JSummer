package com.de.zerosys.JSummer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Custom SWT dialog to allow the user to input strings for various uses.
 */
public class InputDialog extends Dialog {
    private String title   = "Input Dialog";
    private String message = "Please enter a value:";
    private final String initialText;
    private String input;
    
    private int width = 0, height = 0;
    
    /**
     * Custom SWT dialog to allow the user to input strings for a parent object.
     *
     * @param parent the dialog parent shell
     */
    public InputDialog(Shell parent) {
        this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    }
    
    /**
     * Custom SWT dialog to allow the user to input strings for a parent object.
     *
     * @param parent the dialog parent shell
     * @style the dialog style
     */
    public InputDialog(Shell parent, int style) {
        super(parent, style);
        this.initialText = "";
    }
    
    /**
     * Custom SWT dialog to allow the user to input strings for a parent object with a title, message, style
     * and initial text to be displayed.
     *
     * @param parent      the dialog parent shell
     * @param title       the dialog title
     * @param message     the dialog message
     * @param initialText the dialog initialText
     */
    public InputDialog(Shell parent, String title, String message, String initialText) {
        this(parent, title, message, initialText, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    }
    
    /**
     * Custom SWT dialog to allow the user to input strings for a parent object with a title, message, style
     * and initial text to be displayed.
     *
     * @param parent      the dialog parent shell
     * @param title       the dialog title
     * @param message     the dialog message
     * @param initialText the dialog initialText
     * @param style       the dialog style
     */
    public InputDialog(Shell parent, String title, String message, String initialText, int style) {
        super(parent, style);
        this.title       = title;
        this.message     = message;
        this.initialText = initialText;
    }
    
    
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getInput() {
        return input;
    }
    public void setInput(String input) {
        this.input = input;
    }
    
    public void setSize(int width, int height) {
        this.width = width;
        this.height= height;
    }
    public Point getSize() {
        return new Point(width, height);
    }
    
    /**
     * Opens the InputDialog and returns the user's input when the dialog closes.
     *
     * @return the user input data
     */
    public String open() {
        int   style       = getStyle();
        Shell parent      = getParent();
        final Shell shell = new Shell(parent, style);
        shell.setText(title);
        shell.setMinimumSize(shell.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        if (width > 0 && height > 0) {
            shell.setSize(width, height);
        }
        
        createContents(shell);
        
        if ((style & SWT.TRAVERSE_ESCAPE) != 0) {
            shell.addListener(SWT.Traverse, new Listener() {
                public void handleEvent(Event event) {
                    if (event.detail == SWT.TRAVERSE_ESCAPE) {
                        event.doit = false;
                    }
                }
            });
        }
        
        shell.pack();
        
        Rectangle parentBounds = parent.getBounds();
        Point     shellSize    = shell.getSize();
        shell.setLocation((parentBounds.x + (parentBounds.width  / 2)) - (shellSize.x / 2),
                          (parentBounds.y + (parentBounds.height / 2)) - (shellSize.y / 2));
        
        shell.open();
        
        Display display = parent.getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        
        return input;
    }
    
    private void createContents(final Shell shell) {
        shell.setLayout(new GridLayout(2, true));
        
        Label label = new Label(shell, SWT.NONE);
        label.setText(message);
        GridData data = new GridData();
        data.horizontalSpan = 2;
        label.setLayoutData(data);
        
        final Text inputField = new Text(shell, SWT.SINGLE | SWT.BORDER);
        inputField .setText(initialText);
        data = new GridData(GridData.FILL_HORIZONTAL, GridData.FILL_VERTICAL, true, false);
        data.horizontalSpan = 2;
        data.minimumWidth = (this.width > 300) ? this.width : 300;
        inputField .setLayoutData(data);
        
        Button btnOK = new Button(shell, SWT.PUSH);
        btnOK.setText("&OK");
        data = new GridData(GridData.FILL_HORIZONTAL);
        btnOK.setLayoutData(data);
        btnOK.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                input = inputField.getText();
                shell.close();
            }
        });
        
        Button btnCancel = new Button(shell, SWT.PUSH);
        btnCancel.setText("&Cancel");
        data = new GridData(GridData.FILL_HORIZONTAL);
        btnCancel.setLayoutData(data);
        btnCancel.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                input = null;
                shell.close();
            }
        });
        
        shell.setDefaultButton(btnOK);
    }
}