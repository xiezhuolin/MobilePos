package cn.acewill.mobile.pos.printer.gpnetwork;

import android.os.StrictMode;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by aqw on 2016/12/30.
 * 参考:http://www.cnblogs.com/kunyashaw/p/5083552.html
 */
public class GpPrintCommand {
    private InputStream InStream = null;
    private OutputStream OutStream = null;
    private Socket socket = null;
    private String printerstatus = "";
    private byte[] readBuf = new byte[1024];
    private int[] y_point = new int[]{50, 80, 105, 160, 190};//默认设置五行，每行Y坐标
    private int[] y_point3 = new int[]{10,50, 80, 105, 160, 190};//默认设置五行，每行Y坐标
    private int[] y_point2 = new int[]{10, 65, 95, 125, 180};//默认设置五行，每行Y坐标
    private boolean isHeightCm = false;//判断标签纸的高度是否大于30  如果大于需要打取餐号


    //打开端口
    public void openport(String ipaddress, int portnumber) throws IOException {
        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder()).detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy((new StrictMode.VmPolicy.Builder()).detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());

        try {
            if (socket == null) {
                socket = new Socket();
            }
            if (!isAlive(socket)) {
                socket.connect(new InetSocketAddress(ipaddress, portnumber), 5000);
            }
            InStream = socket.getInputStream();
            OutStream = socket.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean isAlive(Socket mSocket) {
        if (mSocket.equals(null) || mSocket.isClosed()
                || !mSocket.isConnected() || mSocket.isInputShutdown()
                || mSocket.isOutputShutdown()) {
            // MyLog.d("mSocket.isConnected()"+mSocket.isConnected());
            return false;
        } else {
            // MyLog.d("mSocket.isConnected()"+mSocket.isConnected());
            return true;
        }
    }

    //发送指令
    public void sendcommand(String message) {
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public void sendcommand(byte[] message) {
        try {
            this.OutStream.write(message);
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    //获取打印机状态
    public String status() {
        byte[] message = new byte[]{(byte) 27, (byte) 33, (byte) 83};

        try {
            this.OutStream.write(message);
        } catch (IOException var4) {
            Log.e("THINBTCLIENT", "ON RESUME: Exception during write.", var4);
        }

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException var3) {
            var3.printStackTrace();
        }

        int tim;
        try {
            while (this.InStream.available() > 0) {
                this.readBuf = new byte[1024];
                tim = this.InStream.read(this.readBuf);
            }
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        if (this.readBuf[0] == 2 && this.readBuf[5] == 3) {
            for (tim = 0; tim <= 7; ++tim) {
                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 64 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 64 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Ready";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 69 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 96 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Head Open";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 64 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 96 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Head Open";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 69 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 72 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Ribbon Jam";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 69 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 68 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Ribbon Empty";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 69 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 65 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "No Paper";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 69 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 66 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Paper Jam";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 69 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 65 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Paper Empty";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 67 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 64 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Cutting";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 75 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 64 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Waiting to Press Print Key";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 76 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 64 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Waiting to Take Label";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 80 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 64 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Printing Batch";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 96 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 64 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Pause";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 69 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 64 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Pause";
                    this.readBuf = new byte[1024];
                    break;
                }
            }
        }

        return this.printerstatus;
    }

    public String batch() {
        boolean printvalue = false;
        String printbatch = "";
        String stringbatch = "0";
        String message = "~HS";
        this.readBuf = new byte[1024];
        byte[] batcharray = new byte[]{(byte) 48, (byte) 48, (byte) 48, (byte) 48, (byte) 48, (byte) 48, (byte) 48, (byte) 48};
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var9) {
            Log.e("THINBTCLIENT", "ON RESUME: Exception during write.", var9);
        }

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException var8) {
            var8.printStackTrace();
        }

        int i;
        try {
            while (this.InStream.available() > 50) {
                this.readBuf = new byte[1024];
                i = this.InStream.read(this.readBuf);
            }
        } catch (IOException var10) {
            var10.printStackTrace();
        }

        if (this.readBuf[0] == 2) {
            System.arraycopy(this.readBuf, 55, batcharray, 0, 8);

            for (i = 0; i <= 7; ++i) {
                if (batcharray[i] == 44) {
                    batcharray = new byte[]{(byte) 57, (byte) 57, (byte) 57, (byte) 57, (byte) 57, (byte) 57, (byte) 57, (byte) 57};
                }
            }

            stringbatch = new String(batcharray);
            int var11 = Integer.parseInt(stringbatch);
            printbatch = Integer.toString(var11);
            if (printbatch == "99999999") {
                printbatch = "";
            }
        }

        return printbatch;
    }

    public void closeport() {
        try {
            this.socket.close();
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    /**
     * 初始化
     *
     * @param width           标签纸宽度/mm  （1mm = 8 dot  例如:宽度60，480个点，高度27,216个点）
     * @param height          标签纸高度/mm
     * @param speed           打印速度 1.5,2,3,4
     * @param density         打印浓度0-15
     * @param sensor
     * @param sensor_distance 两张卷标纸间的垂直间距距离
     * @param sensor_offset   垂直间距的偏移
     */
    public void setup(int width, int height, int speed, int density, int sensor, int sensor_distance, int sensor_offset) {
        String message = "";
        String size = "SIZE " + width + " mm" + ", " + height + " mm";
        String speed_value = "SPEED " + speed;
        String density_value = "DENSITY " + density;
        if(height >= 30)
        {
            isHeightCm = true;
        }
        String sensor_value = "";
        if (sensor == 0) {
            sensor_value = "GAP " + sensor_distance + " mm" + ", " + sensor_offset + " mm";
        } else if (sensor == 1) {
            sensor_value = "BLINE " + sensor_distance + " mm" + ", " + sensor_offset + " mm";
        }

        message = size + "\n" + speed_value + "\n" + density_value + "\n" + sensor_value + "\n";
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var15) {
            var15.printStackTrace();
        }

    }

    //清除影像缓冲区(image buffer)的数据
    public void clearbuffer() {
        String message = "CLS\n";
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    //设置字体方向0：字体向上，1：字体向下
    public void setDirection(int direction) {
        String message = "DIRECTION " + direction + "\n";
        byte[] msgBuffer = message.getBytes();

        sendcommand(msgBuffer);
    }

    //设置原点坐标
    public void setReference(int x, int y) {
        String message = "REFERENCE " + x + "," + y + "\n";
        byte[] msgBuffer = message.getBytes();
        sendcommand(msgBuffer);
    }

    //打印条形码
    public void barcode(int x, int y, String type, int height, int human_readable, int rotation, int narrow, int wide, String string) {
        String message = "";
        String barcode = "BARCODE ";
        String position = x + "," + y;
        String mode = "\"" + type + "\"";
        String height_value = "" + height;
        String human_value = "" + human_readable;
        String rota = "" + rotation;
        String narrow_value = "" + narrow;
        String wide_value = "" + wide;
        String string_value = "\"" + string + "\"";
        message = barcode + position + " ," + mode + " ," + height_value + " ," + human_value + " ," + rota + " ," + narrow_value + " ," + wide_value + " ," + string_value + "\n";
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var22) {
            var22.printStackTrace();
        }

    }

    /**
     * 打印文字
     *
     * @param line    行数，默认4行，从0开始
     * @param x_scale 横向放大倍数1-10，1为不放大
     * @param y_scale 纵向放大倍数1-10，1为不放大
     * @param string  打印内容
     */
    public void printerfont(int line, int x_scale, int y_scale, String string) {
        try {
            String message = "";
            String text = "TEXT ";
            int[] y_pointTemp = new int[6];
            if (isSprinter) {
                y_pointTemp = y_point2;
            } else {
                if(isHeightCm && isCallNumber)
                {
                    y_pointTemp = y_point3;
                }
                else{
                    y_pointTemp = y_point;
                }
            }
            String position = "10," + y_pointTemp[line];
            String size_value = "\"" + FontType.TSS24.getType() + "\"";
            String rota = "0";
            String x_value = "" + x_scale;
            String y_value = "" + y_scale;
            String string_value = "\"" + string + "\"";
            message = text + position + "," + size_value + "," + rota + "," + x_value + "," + y_value + "," + string_value + "\n";
            Log.e("文字", message);
            byte[] msgBuffer = message.getBytes("gb2312");

            this.OutStream.write(msgBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isSprinter = false;
    private boolean isCallNumber = false;

    public void isSprt(boolean isSprinter) {
        this.isSprinter = isSprinter;
    }

    public void isCallNumber (boolean isCallNumber)
    {
        this.isCallNumber = isCallNumber;
    }

    //打印出缓冲区的数据,第一个参数是打印的分数，第二个是每份打印的张数
    public void printlabel(int quantity, int copy) {
        String message = "";
        message = "PRINT " + quantity + ", " + copy + "\n";
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var6) {
            var6.printStackTrace();
        }

    }

    //命令打印机进一张纸
    public void formfeed() {
        String message = "";
        message = "FORMFEED\n";
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    //用来启用/禁用撕纸位置走到撕纸处,此设置关掉电源后将保存在打印机内(ON/OFF)
    public void setTear(String status) {
        String message = "";
        message = "SET TEAR " + status + "\n";
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public void sendfile(String filename) {
        try {
            FileInputStream fis = new FileInputStream("/sdcard/Download/" + filename);
            byte[] data = new byte[fis.available()];

            while (fis.read(data) != -1) {
                ;
            }

            this.OutStream.write(data);
            fis.close();
        } catch (Exception var4) {
            ;
        }

    }

    public void downloadpcx(String filename) {
        try {
            FileInputStream fis = new FileInputStream("/sdcard/Download/" + filename);
            byte[] data = new byte[fis.available()];
            String download = "DOWNLOAD F,\"" + filename + "\"," + data.length + ",";
            byte[] download_head = download.getBytes();

            while (fis.read(data) != -1) {
                ;
            }

            this.OutStream.write(download_head);
            this.OutStream.write(data);
            fis.close();
        } catch (Exception var6) {
            ;
        }

    }

    public void downloadbmp(String filename) {
        try {
            FileInputStream fis = new FileInputStream("/sdcard/Download/" + filename);
            byte[] data = new byte[fis.available()];
            String download = "DOWNLOAD F,\"" + filename + "\"," + data.length + ",";
            byte[] download_head = download.getBytes();

            while (fis.read(data) != -1) {
                ;
            }

            this.OutStream.write(download_head);
            this.OutStream.write(data);
            fis.close();
        } catch (Exception var6) {
            ;
        }

    }

    public void downloadttf(String filename) {
        try {
            FileInputStream fis = new FileInputStream("/sdcard/Download/" + filename);
            byte[] data = new byte[fis.available()];
            String download = "DOWNLOAD F,\"" + filename + "\"," + data.length + ",";
            byte[] download_head = download.getBytes();

            while (fis.read(data) != -1) {
                ;
            }

            this.OutStream.write(download_head);
            this.OutStream.write(data);
            fis.close();
        } catch (Exception var6) {
            ;
        }

    }
}
