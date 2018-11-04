package cn.acewill.mobile.pos.printer.vendor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;


/**
 * 通过指令来控制打印机
 * http://www.cnblogs.com/rinack/p/5227133.html
 * http://www.v5blogs.com/blog/902bf470afb03bf9e79cf05a0b6de2cd
 * http://www.jiacheo.org/blog/594
 * http://blog.csdn.net/xiaoxian8023/article/details/8440625
 * http://blog.csdn.net/laner0515/article/details/8457337 打印位图
 * http://www.cnblogs.com/hnxxcxg/p/3580402.html
 *
 *
 * Created by Acewill on 2016/6/7.
 */
public class WifiPrinter extends CommandPrinter  {
    private String host;
    Socket client;

    public WifiPrinter(String host, int maxCharacterSizePerSize) {
        super(maxCharacterSizePerSize);
        this.host = host;
    }

    public void init() throws IOException {
        client = new Socket();
        client.connect(new InetSocketAddress(host, 9100), 2000);
        client.setSoTimeout(1000); //设置读取超时， 否则read函数会挂起很久
        super.setOutputStream(client.getOutputStream());
        super.setInputStream(client.getInputStream());
    }

    //必须调用close，否则不会真正打印出来
    public void close() throws IOException {
        super.close();
        client.close();
    }



    @Override
    public void openCachBox() throws IOException {
        super.openCachBox();
    }

    @Override
    public void openBuzzer() throws IOException {
        super.openBuzzer();
    }
}
