package net.cyue.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 该类提供了一些与IP地址和MAC地址相关的实用方法，构造函数私有以防止实例化
 */
public class IPUtil {

    // 私有构造函数，防止外部实例化该类
    private IPUtil() {
        // Prevent instantiation
    }

    /**
     * 获取本地主机的IP地址字符串
     * @return 本地主机的IP地址字符串，如果获取失败则返回null
     */
    public static String getLocalHostAddress() {
        // 调用 getLocalHostInetAddress 方法获取本地主机的 InetAddress 对象
        InetAddress inetAddress = getLocalHostInetAddress();
        // 如果 InetAddress 对象不为空，则返回其主机地址字符串，否则返回 null
        return inetAddress != null ? inetAddress.getHostAddress() : null;
    }

    /**
     * 获取本地主机的 InetAddress 对象
     * @return 本地主机的 InetAddress 对象，如果获取失败则返回 null
     */
    public static InetAddress getLocalHostInetAddress() {
        try {
            // 候选地址，用于在没有找到本地地址时返回
            InetAddress candidateAddress = null;
            // 获取所有网络接口的枚举
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();

            // 遍历所有网络接口
            while(ifaces.hasMoreElements()) {
                // 获取当前网络接口
                NetworkInterface iface = ifaces.nextElement();
                // 获取该网络接口的所有 IP 地址的枚举
                Enumeration<InetAddress> inetAddrs = iface.getInetAddresses();

                // 遍历该网络接口的所有 IP 地址
                while(inetAddrs.hasMoreElements()) {
                    // 获取当前 IP 地址
                    InetAddress inetAddr = inetAddrs.nextElement();
                    // 排除回环地址
                    if (!inetAddr.isLoopbackAddress()) {
                        // 如果是本地地址，则直接返回该地址
                        if (inetAddr.isSiteLocalAddress()) {
                            return inetAddr;
                        }

                        // 如果候选地址为空，则将当前地址设为候选地址
                        if (candidateAddress == null) {
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }

            // 如果有候选地址，则返回候选地址
            if (candidateAddress != null) {
                return candidateAddress;
            } else {
                // 否则使用 JDK 提供的方法获取本地主机地址
                InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
                // 如果 JDK 提供的地址为空，则返回 null，否则返回该地址
                if (jdkSuppliedAddress == null) {
                    return null;
                } else {
                    return jdkSuppliedAddress;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据 InetAddress 对象获取对应的 MAC 地址
     * @param inetAddress InetAddress 对象
     * @return MAC 地址的字符串表示，如果获取失败则返回空字符串
     */
    public static String getMacAddress(InetAddress inetAddress) {
        StringBuilder sb = new StringBuilder();

        try {
            // 根据 InetAddress 对象获取对应的网络接口
            NetworkInterface network = NetworkInterface.getByInetAddress(inetAddress);
            // 获取该网络接口的硬件地址（MAC 地址）
            byte[] mac = network.getHardwareAddress();
            System.out.print("Current MAC address : ");

            // 遍历 MAC 地址的每个字节
            for(int i = 0; i < mac.length; ++i) {
                // 将字节转换为十六进制字符串，并添加到 StringBuilder 中
                sb.append(String.format("%02X%s", mac[i], i < mac.length - 1 ? "-" : ""));
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    /**
     * 将整数形式的 IP 地址转换为点分十进制形式的字符串
     * @param i 整数形式的 IP 地址
     * @return 点分十进制形式的 IP 地址字符串
     */
    public static String intToIp(int i) {
        // 通过位运算和逻辑运算将整数转换为点分十进制形式的字符串
        return (i >> 24 & 255) + "." + (i >> 16 & 255) + "." + (i >> 8 & 255) + "." + (i & 255);
    }

    /**
     * 将点分十进制形式的 IP 地址字符串转换为整数形式
     * @param addr 点分十进制形式的 IP 地址字符串
     * @return 整数形式的 IP 地址
     */
    public static int ipToInt(String addr) {
        // 将 IP 地址字符串按点分割成字符串数组
        String[] addrArray = addr.split("\\.");
        // 用于存储转换后的整数 IP 地址
        int num = 0;

        // 遍历 IP 地址字符串数组
        for(int i = 0; i < addrArray.length; ++i) {
            // 计算当前字节的幂次
            int power = 3 - i;
            // 将当前字节转换为整数并乘以对应的幂次，累加到 num 中
            num = (int)(num + (Integer.parseInt(addrArray[i]) % 256) * Math.pow(256.0F, power));
        }

        return num;
    }

}
