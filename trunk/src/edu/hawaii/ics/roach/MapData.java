package edu.hawaii.ics.roach;

// JFC
import java.io.*;


public class MapData {

	public static final int NUM_COL_TILES = 26;
	public static final int NUM_ROW_TILES = 20;

	String 		title;
	int 		time;
	int[][]		lowerTiles;
	int[][]		upperTiles;
	Object[]	enemyListData;


	public MapData() {
		lowerTiles = new int[NUM_COL_TILES][NUM_ROW_TILES];
		upperTiles = new int[NUM_COL_TILES][NUM_ROW_TILES];
	}

	public MapData(String title, int time,
				   int[][] lowerTiles, int[][] upperTiles,
				   Object[] enemyListData) {
		this.title = title;
		this.time = time;
		this.lowerTiles = lowerTiles;
		this.upperTiles = upperTiles;
		this.enemyListData = enemyListData;
	}


	public void save(OutputStream out) throws IOException {
		DataOutputStream dout = new DataOutputStream(out);

        // saving title n time
		dout.writeUTF(title);
        dout.writeInt(time);

		// saving tiles
		for (int j=0;j < NUM_ROW_TILES;j++)
		for (int i=0;i < NUM_COL_TILES;i++) {
            dout.writeShort(lowerTiles[i][j]);
            dout.writeShort(upperTiles[i][j]);
        }

        // saving enemies
		int count = enemyListData.length;
        dout.writeInt(count);
        for (int i=0;i < count;i++) {
            int[] enemy = (int[]) enemyListData[i];
            dout.writeInt(enemy[0]);
            dout.writeInt(enemy[1]);
            dout.writeInt(enemy[2]);
            dout.writeInt(enemy[3]);
		}

        dout.close();
	}

	public void load(InputStream in) throws IOException {
		DataInputStream din = new DataInputStream(in);

		// reading title n time
		title = din.readUTF();
		time = din.readInt();

		// reading tiles
		for (int j=0;j < NUM_ROW_TILES;j++)
		for (int i=0;i < NUM_COL_TILES;i++) {
            lowerTiles[i][j] = din.readShort();
            upperTiles[i][j] = din.readShort();
        }

        // reading enemies
		int count = din.readInt();
        enemyListData = new Object[count];
        for (int i=0;i < count;i++) {
            enemyListData[i] = new int[] { din.readInt(), din.readInt(),
										   din.readInt(), din.readInt() };
		}

        din.close();
	}

}
