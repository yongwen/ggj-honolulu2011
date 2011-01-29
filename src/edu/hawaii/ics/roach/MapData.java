package edu.hawaii.ics.roach;

// JFC
import java.io.*;


public class MapData {

	String 		title;
	int 		time;
	int[][]		lowerTiles;
	int[][]		upperTiles;
	Object[]	enemyListData;


	public MapData() {
		lowerTiles = new int[20][15];
		upperTiles = new int[20][15];
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
		for (int j=0;j < 15;j++)
		for (int i=0;i < 20;i++) {
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
		for (int j=0;j < 15;j++)
		for (int i=0;i < 20;i++) {
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
