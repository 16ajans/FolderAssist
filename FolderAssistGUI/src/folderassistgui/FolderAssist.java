package folderassistgui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class FolderAssist {

	protected Shell shell;
	private Table table;
	
	protected Path wd;
	protected List<Operation> ops;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		Path wd = Path.of(System.getProperty("user.dir"));

		List<Operation> ops = Files.find(wd, 1, (path, attr) -> {
			if (path.getFileName().toString().equals("FolderAssist.jar") || !attr.isRegularFile())
				return false;
			else
				return true;
		}).map(path -> new Operation(path)).collect(Collectors.toList());

		try {
			FolderAssist window = new FolderAssist(wd, ops);
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	FolderAssist(Path wd, List<Operation> ops) {
		this.wd = wd;
		this.ops = ops;
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(1280, 720);
		shell.setText("FolderAssist");
		shell.setLayout(new GridLayout(2, false));

		Label lblPendingOperations = new Label(shell, SWT.NONE);
		lblPendingOperations.setText("Pending Operations in:");

		Label lblPath = new Label(shell, SWT.NONE);
		lblPath.setText(wd.toString());

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		GridData gd_table = new GridData(SWT.CENTER, SWT.CENTER, true, true, 2, 1);
		gd_table.widthHint = 10000;
		gd_table.heightHint = 10000;
		table.setLayoutData(gd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tblclmnFile = new TableColumn(table, SWT.NONE);
		tblclmnFile.setWidth(500);
		tblclmnFile.setText("File");

		TableColumn tblclmnDest = new TableColumn(table, SWT.NONE);
		tblclmnDest.setWidth(500);
		tblclmnDest.setText("Destination");

		List<TableItem> rows = new ArrayList<TableItem>();

		if (ops.isEmpty()) {
			TableItem tableItem = new TableItem(table, SWT.NONE);
			tableItem.setText(new String[] { "No Files Found in Current Directory!" });
		} else {
			for (int i = 0; i < ops.size(); i++) {
				rows.add(new TableItem(table, SWT.NONE));
				rows.get(i).setText(new String[] { ops.get(i).getOrig().toString().substring(wd.toString().length()),
						ops.get(i).getTarget().toString().substring(wd.toString().length()) });
			}
		}

		Button btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setText("Cancel");
		btnCancel.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event ev) {
				shell.close();
			}
		});

		Button btnApply = new Button(shell, SWT.NONE);
		btnApply.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnApply.setText("Apply");
		btnApply.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event ev) {
				for (Operation op : ops) {
			    	try  {
			    		Files.createDirectory(op.getDir());
			    	} catch (FileAlreadyExistsException e) {
			    	} catch (IOException e) {
						e.printStackTrace();
					}
			    	
			    	try {
						Files.move(op.getOrig(), op.getTarget());
					} catch (IOException e) {
 						e.printStackTrace();
					}
			    }
				shell.close();
			}
		});

	}
}
