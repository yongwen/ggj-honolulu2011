package edu.hawaii.ics.roach;

// JFC
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

// GTGE
import com.golden.gamedev.*;
import com.golden.gamedev.engine.graphics.*;
import com.golden.gamedev.object.*;
import com.golden.gamedev.object.background.*;
import com.golden.gamedev.util.*;


public class MapBuilder extends Game implements ListSelectionListener {

	public static final int NUM_COL_TILES = 26;
	public static final int NUM_ROW_TILES = 20;

	public static final int LOWER_TILE_EDITING = 0,
							UPPER_TILE_EDITING = 1,
							ENEMY_EDITING = 2;

	int[][]				lowerTiles = new int[NUM_COL_TILES][NUM_ROW_TILES];
	int[][]				upperTiles = new int[NUM_COL_TILES][NUM_ROW_TILES];
	DefaultListModel	enemyListData = new DefaultListModel();

	BufferedImage[]		lowerImages;
	BufferedImage[]		upperImages;
	BufferedImage[]		enemyImages;

	int 				num;
	int					editingMode;

	TilePicker 			tilePicker;
	EnemyPicker			enemyPicker;

	JFileChooser 		fileChooser;

	Frame				mainFrame;

	boolean				edited;


 /****************************************************************************/
 /**************************** INIT RESOURCES ********************************/
 /****************************************************************************/

	public void initResources() {
		setFPS(30);

		// initial dir is in folder [levels]
		File initialDir = bsIO.setFile("levels");
		if (initialDir.isDirectory() == false) initialDir = bsIO.setFile(".");
		fileChooser = new JFileChooser(initialDir);
		fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
		    public boolean accept(File f) {
				// accept only directory & .map
				return (f.isDirectory() || f.getName().endsWith(".map"));
			}

		    public String getDescription() {
			    return "Roach Map Level";
			}
		} );

		lowerImages = getImages("images/lowertileset.png", 10, 5);
		upperImages = getImages("images/uppertileset.png", 7, 1);
		enemyImages = getImages("images/roach.png", 12, 2);

		mainFrame = ((WindowedMode) bsGraphics).getFrame();
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (confirmEdit("to exit")) {
					System.exit(0);
				}
			}
		} );

		tilePicker = new TilePicker(mainFrame);
		enemyPicker = new EnemyPicker(mainFrame);

		resetMap();

		bsGraphics.getComponent().requestFocus();
	}


	public boolean confirmEdit(String msg) {
		if (edited == false) {
			return true;
		}

		return (JOptionPane.showConfirmDialog(mainFrame,
					"Map is edited and not saved.\nContinue "+msg+"?",
					"Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
	}


	public void editMap() {
		if (!edited) {
			mainFrame.setTitle(mainFrame.getTitle() + " *");
			edited = true;
		}
	}

	public void resetMap() {
		if (confirmEdit("reset map") == false) {
			return;
		}

		for (int j=0;j < NUM_ROW_TILES;j++)
		for (int i=0;i < NUM_COL_TILES;i++) {
			lowerTiles[i][j] = 0;
			upperTiles[i][j] = 0;
		}
		enemyListData.clear();

		tilePicker.lowerList.setSelectedIndex(0);
		tilePicker.mapTitle.setText("");
		tilePicker.mapTime.setText("");

		mainFrame.setTitle("Golden T Game Engine");
		edited = false;
	}

	public void saveMap() {
		MapData map = getValidMap();
		if (map == null) {
			return;
		}

		// open up save file chooser
		Component comp = bsGraphics.getComponent();
        int result = fileChooser.showSaveDialog(comp);
        if (result == JFileChooser.APPROVE_OPTION) {
		try {
			// save to specified file
			File file = fileChooser.getSelectedFile();
			File output = FileUtil.setExtension(file, "map");
	        map.save(new FileOutputStream(output));

			mainFrame.setTitle(output.getName());
			edited = false;
		} catch (Exception e) {
        	JOptionPane.showMessageDialog(comp, "Failed to save: " + e.getMessage());
        } }
	}

	public void loadMap() {
		if (confirmEdit("loading map") == false) {
			return;
		}

        Component comp = bsGraphics.getComponent();

		// open up open file chooser
        int result = fileChooser.showOpenDialog(comp);
        if (result == JFileChooser.APPROVE_OPTION) {
	        File f = fileChooser.getSelectedFile();
			try {
				// load from specified file
				edited = false;
				resetMap();
		        MapData data = new MapData();
		        data.load(new FileInputStream(f));


		        // init map from loaded data
				tilePicker.mapTitle.setText(data.title);
		        tilePicker.mapTime.setText(String.valueOf(data.time));
		        lowerTiles = data.lowerTiles;
		        upperTiles = data.upperTiles;

		        enemyListData.clear();
		        int count = data.enemyListData.length;
				for (int i=0;i < count;i++) {
					enemyListData.addElement(data.enemyListData[i]);
				}

				mainFrame.setTitle(f.getName());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(comp, "Failed to load \""+f+"\":\n" + e.getMessage());
                resetMap();
            }
		}
	}

	// create data map, but first validate all map fields
	private MapData getValidMap() {
		Component comp = bsGraphics.getComponent();

        // check for title
        String title = tilePicker.mapTitle.getText();
		if (title.length() == 0) {
	        JOptionPane.showMessageDialog(comp, "Please specify Map Title!");
	        tilePicker.mapTitle.requestFocus();
	        return null;
		}

        // check for time
		String stime = tilePicker.mapTime.getText();
        int time = 0;
		try {
			time = Integer.parseInt(stime);
			if (time == 0) throw new RuntimeException();
		} catch (Exception e) {
	        JOptionPane.showMessageDialog(comp, "Invalid Game Time! Must be greater than zero (> 0)");
	        tilePicker.mapTime.requestFocus();
	        return null;
		}

		// check for start and end point
		boolean startPoint = true;
		boolean exitPoint = true;
		for (int j=0;j < NUM_ROW_TILES;j++)
		for (int i=0;i < NUM_COL_TILES;i++) {
			if (upperTiles[i][j] == 2) {
				exitPoint = true;
			} else if (upperTiles[i][j] == 7) {
				startPoint = true;
			}
		}
		if (startPoint == false) {
			JOptionPane.showMessageDialog(comp, "Please specify player start point!");
			tilePicker.upperList.setSelectedIndex(7);
			return null;
		}

		if (exitPoint == false) {
			JOptionPane.showMessageDialog(comp, "Please specify player exit point!");
			tilePicker.upperList.setSelectedIndex(2);
			return null;
		}

		MapData data = new MapData(title,time,lowerTiles,upperTiles,enemyListData.toArray());

		return data;
	}

	public void runMap() {
		final MapData data = getValidMap();
		if (data == null) {
			return;
		}

		stop();
		new Thread() {
			public void run() {
				RoachGame roach = new RoachGame() {
					protected void notifyExit() {
						MapBuilder.this.start();
					}
				};
				roach.nextGameID = RoachGame.ROACH_GAME;
				roach.testLevel = data;

				GameLoader game = new GameLoader();
				game.setup(roach, new Dimension(640,480), false);
				game.start();
			}
		}.start();
	}


 /****************************************************************************/
 /************************** UPDATE MAP BUILDER ******************************/
 /****************************************************************************/

	public void update(long elapsedTime) {
	try {
		int tileX = (int) (getMouseX() / 24);
		int tileY = (int) (getMouseY() / 24);
		if (tileX >= NUM_COL_TILES) tileX = NUM_COL_TILES-1;
		if (tileY >= NUM_ROW_TILES) tileY = NUM_ROW_TILES-1;


		// placing a tile
		if (bsInput.isMouseDown(MouseEvent.BUTTON1)) {
			switch (editingMode) {
				case LOWER_TILE_EDITING:
					lowerTiles[tileX][tileY] = num;
					editMap();
				break;

				case UPPER_TILE_EDITING:
					upperTiles[tileX][tileY] = num;
					editMap();
				break;

				case ENEMY_EDITING:
					int[] enemy = (int[]) enemyListData.get(num);
					enemy[2] = tileX;
					enemy[3] = tileY;
					editMap();
				break;
			}
		}


		// eye dropper
		if (rightClick()) {
			switch (editingMode) {
				case LOWER_TILE_EDITING:
					num = lowerTiles[tileX][tileY];
					tilePicker.lowerList.setSelectedIndex(num);
					tilePicker.lowerList.ensureIndexIsVisible(num);
					tilePicker.lowerList.repaint();
				break;

				case UPPER_TILE_EDITING:
					num = upperTiles[tileX][tileY];
					tilePicker.upperList.setSelectedIndex(num);
					tilePicker.upperList.ensureIndexIsVisible(num);
					tilePicker.upperList.repaint();
				break;

				case ENEMY_EDITING:
					int size = enemyListData.size();
					for (int i=0;i < size;i++) {
						int[] enemy = (int[]) enemyListData.get(i);
						if (enemy[2] == tileX && enemy[3] == tileY) {
							enemyPicker.enemyList.setSelectedIndex(i);
							break;
						}
					}
				break;
			}
		}

		// run game
		if (keyPressed(KeyEvent.VK_F5) ||
			(keyPressed(KeyEvent.VK_R) && bsInput.isKeyDown(KeyEvent.VK_CONTROL))) {
			runMap();
		}

	} catch (Exception e) {
//		e.printStackTrace();
	} }


 /****************************************************************************/
 /**************************** RENDER MAP BUILDER ****************************/
 /****************************************************************************/

	public void render(Graphics2D g) {
	try {
		// clear background
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());

		// draw tiles
		for (int j=0;j < NUM_ROW_TILES;j++)
		for (int i=0;i < NUM_COL_TILES;i++) {
			int tile = lowerTiles[i][j];
			g.drawImage(lowerImages[tile], i*24, j*24, null);

			tile = upperTiles[i][j];
			g.drawImage(upperImages[tile], i*24, j*24, null);
		}

		// draw tile position
		int tileX = (int) (getMouseX() / 24);
		int tileY = (int) (getMouseY() / 24);

		// draw enemies
		int size = enemyListData.size();
		for (int i=0;i < size;i++) {
			int[] enemy = (int[]) enemyListData.get(i);
			int img = (enemy[0]*12)+7;
			int x = enemy[2]*24;
			int y = enemy[3]*24;

			g.drawImage(enemyImages[img], x, y, null);
		}


		g.setColor(Color.YELLOW);
		if (editingMode != ENEMY_EDITING) {
			g.drawRect(tileX*24, (tileY*24)+8, 24, 24);
		} else {
			int[] enemy = (int[]) enemyListData.get(num);
			g.drawRect(enemy[2]*24, enemy[3]*24, 24, 32);
		}
	} catch (Exception e) {
//		e.printStackTrace();
	} }


 /****************************************************************************/
 /***************************** MAIN-CLASS ***********************************/
 /****************************************************************************/

	public static void main(String[] args) {
		GameLoader game = new GameLoader();
		game.setup(new MapBuilder(), new Dimension(640,480), false);
		((WindowedMode) game.getGame().bsGraphics).getFrame().removeWindowListener(game);
		game.start();
	}


 /****************************************************************************/
 /*************************** COMMON GUI METHODS *****************************/
 /****************************************************************************/

	private JComponent create(JComponent comp, int x, int y, int w, int h) {
		comp.setBounds(x,y,w,h);

		return comp;
	}

    // used to render list of tiles and enemy detail
	private class CellRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object o,
													  int index, boolean selected,
													  boolean focus) {
            // get the standard cell renderer to render this cell
			JLabel label = (JLabel) super.getListCellRendererComponent(list, o, index, selected, focus);
			if (o == null) return label;

			JLabel value = (JLabel) o; // tile/enemy to be drawn

			label.setText(String.valueOf(index));
			if (index == -1) {
				label.setText("");
			}
            label.setIcon(value.getIcon());

            return label;
        }

    }

    // used to render list of enemies
	private class EnemyListRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object o,
													  int index, boolean selected,
													  boolean focus) {
            // get the standard cell renderer to render this cell
			JLabel label = (JLabel) super.getListCellRendererComponent(list, o, index, selected, focus);

			int[] value = (int[]) o; // enemy list -> charImage-logic

            label.setIcon(((JLabel) enemyPicker.charList.getItemAt(value[0])).getIcon());
			label.setText((String) enemyPicker.logicList.getItemAt(value[1]));

            return label;
        }

    }

	// when lowerTile/upperTile/enemyList is clicked/changed
	public void valueChanged(ListSelectionEvent e) {
		JList list = (JList) e.getSource();
		int index = list.getSelectedIndex();
		if (e.getValueIsAdjusting() || index == -1) return;
		list.ensureIndexIsVisible(index);

		if (list == tilePicker.lowerList ||
			list == tilePicker.upperList) {
			// lower/upper tile mode selected
			editingMode = (list == tilePicker.lowerList) ?
						  LOWER_TILE_EDITING : UPPER_TILE_EDITING;

			JLabel label = (JLabel) list.getModel().getElementAt(index);
			num = Integer.parseInt(label.getText());

			enemyPicker.charList.setEnabled(false);
			enemyPicker.logicList.setEnabled(false);
			enemyPicker.charList.setSelectedIndex(-1);
			enemyPicker.logicList.setSelectedIndex(-1);

		} else if (list == enemyPicker.enemyList) {
			// enemy list mode selected
			int[] enemy = (int[]) enemyListData.get(index);

			editingMode = ENEMY_EDITING;
			num = index;

			enemyPicker.charList.setEnabled(true);
			enemyPicker.logicList.setEnabled(true);
			enemyPicker.charList.setSelectedIndex(enemy[0]);
			enemyPicker.logicList.setSelectedIndex(enemy[1]);
		}


		// clear focus from previous selected object
		if (list != tilePicker.lowerList) tilePicker.lowerList.clearSelection();
		if (list != tilePicker.upperList) tilePicker.upperList.clearSelection();
		if (list != enemyPicker.enemyList) enemyPicker.enemyList.clearSelection();
	}


 /****************************************************************************/
 /****************************** TOOLBAR *************************************/
 /****************************************************************************/

	// tile picker dialog
	class TilePicker extends JDialog {

		JTextField	mapTitle, mapTime;
		JList		lowerList, upperList;

		public TilePicker(Frame owner) {
			super(owner, "Tile Picker");

			setResizable(false);
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

			JPanel contentPane = new JPanel();
			// use absolute position
			contentPane.setLayout(null);

				// map title
				contentPane.add(create(new JLabel("Map Title:"), 5, 5, 100, 20));
				mapTitle = (JTextField) create(new JTextField(), 5, 25, 100, 20);
				contentPane.add(mapTitle);

				// map timer
				contentPane.add(create(new JLabel("Map Time:"), 5, 50, 100, 20));
				mapTime = (JTextField) create(new JTextField(), 5, 70, 100, 20);
				contentPane.add(mapTime);

				// lower tile toolbar
				JScrollPane lowerScrollPane = createTileToolBar(5, 95, 100, 128,
												  				lowerImages);
				lowerList = (JList) lowerScrollPane.getViewport().getView();
				contentPane.add(lowerScrollPane);

				// upper tile toolbar
				JScrollPane upperScrollPane = createTileToolBar(5, 228, 100, 128,
												  				upperImages);
				upperList = (JList) upperScrollPane.getViewport().getView();
				contentPane.add(upperScrollPane);

			setContentPane(contentPane);
			pack();
			Point p = owner.getLocationOnScreen();
			setBounds(p.x-getWidth(), p.y, 100, 400);
			setVisible(true);
		}


		// construct the tile toolbar
		private JScrollPane createTileToolBar(int x, int y, int w, int h,
											  BufferedImage[] tileImages) {
			DefaultListModel data = new DefaultListModel();
			for (int i=0;i < tileImages.length;i++) {
				// text of the tile -> tileNum
				JLabel lbTile = new JLabel(String.valueOf(i),
										   new ImageIcon(tileImages[i]),
										   JLabel.LEFT);
				data.addElement(lbTile);
			}

			JList list = new JList(data);
			list.setCellRenderer(new CellRenderer());
			list.addListSelectionListener(MapBuilder.this);

			JScrollPane scrollPane = new JScrollPane(list,
													 JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
													 JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setBounds(x, y, w, h);

			return scrollPane;
		}

	}


	// enemy picker dialog
	class EnemyPicker extends JDialog implements ActionListener {

		JButton		btnAdd, btnDelete;
		JButton 	btnLoad, btnSave, btnReset;
		JButton		btnRun;
		JList		enemyList;
		JComboBox	charList, logicList;


		public EnemyPicker(Frame owner) {
			super(owner, "Enemy Picker");

			setResizable(false);
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

			JPanel contentPane = new JPanel();
			contentPane.setLayout(null);

				// enemy list
				contentPane.add(create(new JLabel("Enemy List:"), 5, 5, 100, 20));
				enemyList = new JList(enemyListData);
				enemyList.setCellRenderer(new EnemyListRenderer());
				enemyList.addListSelectionListener(MapBuilder.this);
				JScrollPane scrollPane = new JScrollPane(enemyList);
				scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				scrollPane.setBounds(5, 25, 100, 110);
				contentPane.add(scrollPane);

				// new-delete button
				btnAdd = (JButton) create(new JButton("Add"), 30, 136, 75, 25);
				btnAdd.addActionListener(this);
				contentPane.add(btnAdd);
				btnDelete = (JButton) create(new JButton("Delete"), 30, 161, 75, 25);
				btnDelete.addActionListener(this);
				contentPane.add(btnDelete);

				// enemy char combo box
				charList = createEnemyComboBox(5, 190, 100, 40);
				charList.addActionListener(this);
				charList.setSelectedIndex(-1);
				charList.setEnabled(false);
				contentPane.add(charList);

				// enemy logic combo box
				logicList = createEnemyLogic(5, 230, 100, 30);
				logicList.addActionListener(this);
				logicList.setSelectedIndex(-1);
				logicList.setEnabled(false);
				contentPane.add(logicList);

				btnLoad = (JButton) create(new JButton("Load"), 5, 265, 100, 25);
				btnLoad.addActionListener(this);
				contentPane.add(btnLoad);

				btnSave = (JButton) create(new JButton("Save"), 5, 290, 100, 25);
				btnSave.addActionListener(this);
				contentPane.add(btnSave);

				btnReset = (JButton) create(new JButton("Reset"), 5, 315, 100, 25);
				btnReset.addActionListener(this);
				contentPane.add(btnReset);

				btnRun = (JButton) create(new JButton("Run"), 5, 340, 100, 25);
				btnRun.addActionListener(this);
				contentPane.add(btnRun);

			setContentPane(contentPane);
			pack();
			Point p = owner.getLocationOnScreen();
			setBounds(p.x+MapBuilder.this.getWidth()+5, p.y, 100, 400);
			setVisible(true);
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnAdd) {
				// add default enemy
				int[] enemy = new int[] { 0, 0, 0, 0 };
				enemyListData.addElement(enemy);
				enemyList.setSelectedIndex(enemyListData.size()-1);
				editMap();

			} else if (e.getSource() == btnDelete) {
				// remove enemy from the list
				int index = enemyList.getSelectedIndex();
				if (index != -1) {
					enemyListData.remove(index);
				}

				if (enemyListData.size() == 0) {
					// enemy removed, and no enemy left
					// change to lower tile edit mode
					tilePicker.lowerList.setSelectedIndex(0);

				} else {
					if (num > enemyListData.size()-1) {
						num = enemyListData.size()-1;
					}
					enemyList.setSelectedIndex(num);
				}
				editMap();

			} else if (e.getSource() == btnSave) {
				// save level
				saveMap();

			} else if (e.getSource() == btnLoad) {
				// loading level
				loadMap();

			} else if (e.getSource() == btnReset) {
				// reset level
				resetMap();

			} else if (e.getSource() == btnRun) {
				// run the game
				runMap();

			} else if (editingMode == ENEMY_EDITING) {
				// changing enemy image or logic
				int[] enemy = (int[]) enemyListData.get(num);

				if (e.getSource() == charList) {
					enemy[0] = charList.getSelectedIndex();

				} else if (e.getSource() == logicList) {
					enemy[1] = logicList.getSelectedIndex();
				}

				enemyList.repaint();
				editMap();
			}
		}


		// enemy detail character combo box -> vamp, witch, etc
		private JComboBox createEnemyComboBox(int x, int y, int w, int h) {
			Object[] obj = new Object[enemyImages.length/12];
			for (int i=0;i < obj.length;i++) {
				obj[i] = new JLabel(String.valueOf(i),
								    new ImageIcon(enemyImages[(i*12)+7]),
									JLabel.LEFT);
			}

			JComboBox combo = (JComboBox) create(new JComboBox(obj), x, y, w, h);
			combo.setRenderer(new CellRenderer());

			return combo;
		}

		// enemy detail logic list -> turner, up-down patrol, etc
		private JComboBox createEnemyLogic(int x, int y, int w, int h) {
			String[] logic = new String[] { "Stand Still", "Left-Right Patrol",
					"Up-Down Patrol", "Turn Left Always", "Turn Right Always",
					"Turner", "Zombie" };

			JComboBox combo = (JComboBox) create(new JComboBox(logic), x, y, w, h);

			return combo;
		}

	}

}
