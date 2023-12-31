package ClientSide.SystemClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.String;
import java.util.ArrayList;
import java.util.Objects;

import org.json.*;


/**
 * DisplayInterface class provides static methods for displaying query results in the parking management system.
 */
public class DisplayInterface {

    /**
     * Queries and displays the entry time information for a given license plate.
     *
     * @param licensePlate The license plate of the vehicle for which entry time is queried.
     */
    public static void queryVehicleInParkingLot(String licensePlate) {
        // Send query request to the server
        JSONObject sendMessage = new JSONObject();
        sendMessage.put("TYPE","QUERY_ENTRY_TIME");
        sendMessage.put("DATA",licensePlate);
        Client.sendMessage(sendMessage.toString());
        // Receive response from the server
        JSONObject jsonObject = new JSONObject(Objects.requireNonNull(Client.receiveMessage()));
        String str = jsonObject.getString("TYPE");
        System.out.println(str);
        try {
            String data = jsonObject.getString("DATA");
            JOptionPane.showMessageDialog(null, "车辆入库信息：" + "\n" + data,
                    "停车管理系统", JOptionPane.INFORMATION_MESSAGE);
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "未找到相关车辆信息", "停车管理系统",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Queries and displays information about the parking lot, including total and available parking spaces.
     */
    public static void queryParkingLot() {
        // Send query request to the server
        JSONObject sendMessage = new JSONObject();
        sendMessage.put("TYPE","QUERY_PARKING_LOT");

        Client.sendMessage(sendMessage.toString());
        JSONObject jsonObject = new JSONObject(Objects.requireNonNull(Client.receiveMessage()));
        String str = jsonObject.getString("TYPE");
        String message = jsonObject.getString("DATA");
        // Display the parking lot information or an error message
        if (message != null && str.equals("PARKING_LOT")) {
            String[] parts = message.split(","); // Assuming data is separated by commas
            int totalParkingSpaces = Integer.parseInt(parts[0]);
            int availableSpaces = Integer.parseInt(parts[1]);

            JOptionPane.showMessageDialog(null, "总共停车位数量：" +
                            "\n" + totalParkingSpaces + "\n" + availableSpaces,
                    "停车管理系统", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "网络连接错误", "停车管理系统",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Simulates password authentication by sending user credentials to the server.
     * Replace this method with your actual authentication logic.\
     * 发送{TYPE:AUTHENTICATION,DATA:account+","+password}
     * 接收到{TYPE:PASS}OR{TYPE:NO_PASS}
     *
     * @param username The entered username.
     * @param password The entered password.
     * @return True if authentication is successful, false otherwise.
     */
    public static boolean passwordAuthentication(String username, String password) {
        // Replace the following lines with your authentication logic
        // (e.g., contacting a server for authentication)

        JSONObject sendMessage = new JSONObject();
        sendMessage.put("TYPE","AUTHENTICATION");
        sendMessage.put("DATA",username + "," + password);
        Client.sendMessage(sendMessage.toString());
        JSONObject jsonObject = new JSONObject(Objects.requireNonNull(Client.receiveMessage()));
        String isPassed = jsonObject.getString("TYPE");
        System.out.println(isPassed);
        return isPassed != null && isPassed.equals("PASS");
    }

    public static void queryTotalRevenue(String startTime,String endTime){
        JSONObject sendMessage = new JSONObject();
        sendMessage.put("TYPE","QUERY_TOTAL_REVENUE");
        ArrayList<String> a = new ArrayList<>();
        a.add(startTime);
        a.add(endTime);
        sendMessage.put("DATA",a);

        Client.sendMessage(sendMessage.toString());
        JSONObject jsonObject = new JSONObject(Objects.requireNonNull(Client.receiveMessage()));
        String str = jsonObject.getString("TYPE");
        String data = jsonObject.getString("DATA");
        // Display the parking lot information or an error message
        if (data != null && str.equals("TOTAL_REVENUE")) {
            JOptionPane.showMessageDialog(null,"总收入为： "+data,
                    "停车管理系统", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "网络连接错误", "停车管理系统",
                    JOptionPane.ERROR_MESSAGE);
        }
    }



    public static void queryVehicleRevenue(String license, String number, JFrame jf){
        jf.setVisible(false);

        // Display the parking lot information or an error message

        JSONObject sendMessage = new JSONObject();
        sendMessage.put("TYPE","QUERY_VEHICLE_REVENUE");

        sendMessage.put("DATA",license+","+number);

        Client.sendMessage(sendMessage.toString());

        JSONObject jsonObject = new JSONObject(Objects.requireNonNull(Client.receiveMessage()));
        String message = jsonObject.getString("DATA");
        message = message.replace("[","");
        message = message.replace("]","");
        System.out.println(message);
        // 解析数据
        String[] rows = message.split(", ");
        String[][] data = new String[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            data[i] = rows[i].split(",");
        }

        JFrame frame = getFrame(data);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                jf.setVisible(true);
            }
        });
    }

    public static void shutDownTheParkingSystem(){
        Client.sendMessage("{\"TYPE\":\"SHUT_DOWN\"}");
    }

    private static JFrame getFrame(String[][] data) {
        JFrame frame = new JFrame("Bill Records");
        frame.setSize(900, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // 创建一个默认的表格模型
        DefaultTableModel tableModel = new DefaultTableModel(
                data, new String[]{"License", "Start", "End", "TotalRevenue"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 设置表格不可编辑
            }
        };

        // 创建一个 JTable，并使用默认的表格模型
        JTable table = new JTable(tableModel);

        // 创建一个滚动窗格，并将表格添加到滚动窗格中
        JScrollPane scrollPane = new JScrollPane(table);

        // 将滚动窗格添加到 JFrame 中
        frame.add(scrollPane, BorderLayout.CENTER);

        // 设置 JFrame 可见
        frame.setVisible(true);
        return frame;
    }

    public static void queryVIPUsersForForUsersView(JFrame jf){
        jf.setVisible(false);

        // Send query request to the server
        Client.sendMessage("{\"TYPE\":\"SHOW_VIP_USERS\"}");
        // Receive response from the server
        String receivedData = Client.receiveMessage();
        if(receivedData!=null) {
            String[] rows = getStrings(receivedData);
            System.out.println(rows.length);
            int k = 0;

            String[][] data = new String[(rows.length) / 2][2];
            for (int i = 0; i < (rows.length) / 2; i++) {
                for (int j = 0; j < 2; j++) {
                    data[i][j] = rows[k];
                    k++;
                }
            }

            JFrame frame = getVIPUsersFrame(data);

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    super.windowClosed(e);
                    jf.setVisible(true);
                }
            });
        }else {
            JOptionPane.showMessageDialog(null, "网络连接错误", "停车管理系统",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String[] getStrings(String receivedData) {
        JSONObject jsonObject = new JSONObject(receivedData);
    /*  jsonObject示例如下：{"DATA":"[licensePlate:a,balance:123.0, licensePlate:n,balance:123.0, licensePlate:v,balance:123.0]",
                            "TYPE":"VIP_USERS"}*/
        //System.out.println(jsonObject);
        String message = jsonObject.getString("DATA");
        /*  message示例如下：[licensePlate:a,balance:123.0, licensePlate:n,balance:123.0, licensePlate:v,balance:123.0] */
        // System.out.println(message);

        // 删除头部和尾部的 “[” 和 “]”
        message = message.replaceFirst("\\[", " ");// 使表格数据可以对齐
        message = message.substring(0, message.length() - 1);


        // 解析数据
        return message.split(",");
    }

    public static void queryVIPUsersForBalanceAddition(String licensePlate, String amount){
        Client.sendMessage("{\"TYPE\":\"ADD_VIP_USER\",\"DATA\":"+"\"" + licensePlate + "," +amount+"\""+"}");
        String receivedData = Client.receiveMessage();
        if (receivedData != null) {
            JOptionPane.showMessageDialog(null,receivedData,
                    "停车管理系统", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "网络连接错误", "停车管理系统",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // 创建一个表格，直观得展示vip用户和vip用户所剩余余额
    private static JFrame getVIPUsersFrame(String[][] data) {
        JFrame frame = new JFrame("VIP Users Form");
        frame.setSize(450, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // 创建一个默认的表格模型
        DefaultTableModel tableModel = new DefaultTableModel(
                data, new String[]{"LicensePlate", "Balance"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 设置表格不可编辑
            }
        };

        // 创建一个 JTable，并使用默认的表格模型
        JTable table = new JTable(tableModel);

        // 创建一个滚动窗格，并将表格添加到滚动窗格中
        JScrollPane scrollPane = new JScrollPane(table);

        // 将滚动窗格添加到 JFrame 中
        frame.add(scrollPane, BorderLayout.CENTER);

        // 设置 JFrame 可见
        frame.setVisible(true);
        return frame;
    }
}

