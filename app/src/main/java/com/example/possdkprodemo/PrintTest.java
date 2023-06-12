package com.example.possdkprodemo;

import android.content.Context;
import android.util.Log;

import com.possdkpro.POSSDK;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import static com.possdkpro.ConstValue.IMAGE_TYPE_FLASH;
import static com.possdkpro.ConstValue.IMAGE_TYPE_RAM;
import static com.possdkpro.ConstValue.SUCCESS;

public class PrintTest {
    public POSSDK possdk;
    public int handle;
    public PrintTest(POSSDK possdkpro) {
        possdk = possdkpro;
    }
    public int EnumDevice(int portType, String[] deviceInfo, int deviceInfoLen){
        return possdk.EnumDevice(portType,deviceInfo,deviceInfoLen);
    }

    public int OpenPrinter(String modelName, String portInfo) {
        return possdk.OpenPrinter(modelName,portInfo);
    }
    public int ClosePrinter() {

        return possdk.ClosePrinter(handle);
    }
    private String copyAssetGetFilePath(String fileName,Context context) {
        try {
            File cacheDir = context.getCacheDir();
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            File outFile = new File(cacheDir, fileName);
            if (!outFile.exists()) {
                boolean res = outFile.createNewFile();
                if (!res) {
                    return null;
                }
            } else {
                if (outFile.length() > 10) {//表示已经写入一次
                    return outFile.getPath();
                }
            }
            InputStream is = context.getAssets().open(fileName);
            FileOutputStream fos = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();
            return outFile.getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public int Sample_PrintImage(Context context, byte[] errorStatus, int[] statusLength){
        int result = SUCCESS;
        int nStatus = 0;

        nStatus = possdk.QueryStatus(handle, errorStatus,statusLength);
        String status = new String(errorStatus,0,statusLength[0]);
        if (!status.contains("Normal")) {
            return nStatus;
        }
        // Alignment and offset
        String alginFileName = copyAssetGetFilePath("01.bmp",context);
        possdk.PrintText(handle, "=====================\n", "");
        possdk.PrintText(handle, "Alignment and offset, x = -1\n", "");
        possdk.PrintImageFile(handle, alginFileName, "x=-1");

        possdk.PrintText(handle, "Alignment and offset, x = 50\n", "");
        possdk.PrintImageFile(handle, alginFileName, "x=50");

        possdk.PrintText(handle, "Alignment and offset, x = -2\n", "");
        possdk.PrintImageFile(handle, alginFileName, "x=-2");

        possdk.PrintText(handle, "Alignment and offset, x = -3\n", "");
        possdk.PrintImageFile(handle, alginFileName, "x=-3");


        // Zoom
        possdk.PrintText(handle, "=====================\n", "");
        String zoomFileName = copyAssetGetFilePath("01.bmp",context);
        possdk.PrintText(handle, "Zoom, Scale = 20\n", "");
        possdk.PrintImageFile(handle, zoomFileName, "x=-2|Scale=20");

        possdk.PrintText(handle, "Zoom, Scale = 50\n", "");
        possdk.PrintImageFile(handle, zoomFileName, "x=-2|Scale=50");

        possdk.PrintText(handle, "Zoom, Scale = 100\n", "");
        possdk.PrintImageFile(handle, zoomFileName, "x=-2|Scale=100");

        possdk.PrintText(handle, "Zoom, Scale = 200\n", "");
        possdk.PrintImageFile(handle, zoomFileName, "x=-2|Scale=200");

        possdk.PrintText(handle, "Zoom, Width = 100, Height = 200\n", "");
        possdk.PrintImageFile(handle, zoomFileName, "x=-2|Width=100|Height=200");

        possdk.PrintText(handle, "Zoom, Width = 200, Height = 100\n", "");
        possdk.PrintImageFile(handle, zoomFileName, "x=-2|Width=200|Height=100");


        // Dither
        possdk.PrintText(handle, "=====================\n", "");
        String DitherFileName = copyAssetGetFilePath("dither.jpg",context);
        possdk.PrintText(handle, "Dither, Dither = 0, Threshold = -1\n", "");
        possdk.PrintImageFile(handle, DitherFileName, "x=-2|Dither=0|Threshold=-1");

        possdk.PrintText(handle, "Dither, Dither = 0, Threshold = 100\n", "");
        possdk.PrintImageFile(handle, DitherFileName, "x=-2|Dither=0|Threshold=100");

        possdk.PrintText(handle, "Dither, Dither = 1\n", "");
        possdk.PrintImageFile(handle, DitherFileName, "x=-2|Dither=1");

        possdk.PrintText(handle, "Dither, Dither = 2\n", "");
        possdk.PrintImageFile(handle, DitherFileName, "x=-2|Dither=2");

        possdk.PrintText(handle, "Dither, Dither = 3\n", "");
        possdk.PrintImageFile(handle, DitherFileName, "x=-2|Dither=3");


        // Type
        possdk.PrintText(handle, "=====================\n", "");
        possdk.PrintText(handle, "PNG\n", "");
        possdk.PrintImageFile(handle, copyAssetGetFilePath("1.png",context), "x=-2|Dither=3");

        possdk.PrintText(handle, "JPEG\n", "");
        possdk.PrintImageFile(handle, copyAssetGetFilePath("2.jpg",context), "x=-2|Dither=3");

        possdk.PrintText(handle, "TIF\n", "");
        possdk.PrintImageFile(handle, copyAssetGetFilePath("3.tif",context), "x=-2|Dither=3");

        possdk.PrintText(handle, "GIF\n", "");
        possdk.PrintImageFile(handle, copyAssetGetFilePath("4.gif",context), "x=-2|Dither=3");

        // RAM
        possdk.PrintText(handle, "=====================\n", "");
        possdk.PrintText(handle, "DownLoadImage RAM\n", "");
        String ramPath = copyAssetGetFilePath("RAM1.bmp",context)+"|"+
                copyAssetGetFilePath("RAM2.bmp",context)+"|"+
                copyAssetGetFilePath("RAM3.bmp",context);
        result = possdk.DownloadImage(handle, IMAGE_TYPE_RAM, ramPath, "");
        if (result == SUCCESS) {
            result = possdk.PrintDownloadedImage(handle, IMAGE_TYPE_RAM, 1, "X=-2");
            result = possdk.PrintDownloadedImage(handle, IMAGE_TYPE_RAM, 2, "X=-2");
            result = possdk.PrintDownloadedImage(handle, IMAGE_TYPE_RAM, 3, "X=-2");
        } else {
            possdk.PrintText(handle, "DownLoadImage failed", "");
        }

        // Flash
        possdk.PrintText(handle, "=====================\n", "");
        possdk.PrintText(handle, "DownLoadImage Flash\n", "");
        String flashPath = copyAssetGetFilePath("Flash1.bmp",context)+"|"+
                copyAssetGetFilePath("Flash2.bmp",context)+ "|"+
                copyAssetGetFilePath("Flash3.bmp",context);
        result = possdk.DownloadImage(handle, IMAGE_TYPE_FLASH, flashPath, "");
        if (result == SUCCESS) {
            result = possdk.PrintDownloadedImage(handle, IMAGE_TYPE_FLASH, 1, "X=-2");
            result = possdk.PrintDownloadedImage(handle, IMAGE_TYPE_FLASH, 2, "X=-2");
            result = possdk.PrintDownloadedImage(handle, IMAGE_TYPE_FLASH, 3, "X=-2");
        } else {
            possdk.PrintText(handle, "DownLoadImage failed", "");
        }

        //切纸 Cut Paper
        result = possdk.PaperCut(handle, 0, 0);
        return result;
    }

    //打印条码样张
    public int Sample_PrintBarCode(byte[] errorStatus,int[] statusLength)
    {
        int result = SUCCESS;
        int nStatus = 0;

        nStatus = possdk.QueryStatus(handle, errorStatus,statusLength);
        String status = new String(errorStatus,0,statusLength[0]);
        if (!status.contains("Normal")) {
            return nStatus;
        }
        possdk.PrintText(handle, "UPC-A\n", "");
        String data_UPCA = "123456789012";
        result = possdk.BarCodePrint(handle, BarCodeSymbology.BarCode_UPCA, data_UPCA, data_UPCA.length(), "BasicWidth=3|Height=60");
        possdk.FeedLines(handle, 1);

        possdk.PrintText(handle, "UPC-E\n", "");
        String data_UPCE = "023456789012";
        result = possdk.BarCodePrint(handle, BarCodeSymbology.BarCode_UPCE, data_UPCE, data_UPCE.length(), "BasicWidth=3|Height=60");
        possdk.FeedLines(handle, 1);

        possdk.PrintText(handle, "EAN13\n", "");
        String data_EAN13 = "3456789012345";
        result = possdk.BarCodePrint(handle, BarCodeSymbology.BarCode_EAN13, data_EAN13, data_EAN13.length(), "BasicWidth=3|Height=60");
        possdk.FeedLines(handle, 1);

        possdk.PrintText(handle, "EAN8\n", "");
        String data_EAN8 = "12345678";
        result = possdk.BarCodePrint(handle, BarCodeSymbology.BarCode_EAN8, data_EAN8, data_EAN8.length(), "BasicWidth=3|Height=60");
        possdk.FeedLines(handle, 1);

        possdk.PrintText(handle, "CODE39\n", "");
        String data_CODE39 = "01234ABCDE $%+-./";
        result = possdk.BarCodePrint(handle, BarCodeSymbology.BarCode_Code39, data_CODE39, data_CODE39.length(), "BasicWidth=2|Height=60");
        possdk.FeedLines(handle, 1);

        possdk.PrintText(handle, "ITF\n", "");
        String data_ITF = "01234567891234";
        result = possdk.BarCodePrint(handle, BarCodeSymbology.BarCode_ITF, data_ITF, data_ITF.length(), "BasicWidth=3|Height=60");
        possdk.FeedLines(handle, 1);

        possdk.PrintText(handle, "CODEBAR\n", "");
        String data_CODEBAR = "A0123456789$+-./:D";
        result = possdk.BarCodePrint(handle, BarCodeSymbology.BarCode_Codebar, data_CODEBAR, data_CODEBAR.length(), "BasicWidth=2|Height=60");
        possdk.FeedLines(handle, 1);

        possdk.PrintText(handle, "CODE93\n", "");
        String data_CODE93 = "01234ABCDE $%+-./";
        result = possdk.BarCodePrint(handle, BarCodeSymbology.BarCode_Code93, data_CODE93, data_CODE93.length(), "BasicWidth=3|Height=60");
        possdk.FeedLines(handle, 1);

        possdk.PrintText(handle, "CODE128\n", "");
        String data_CODE128 = "01234ABCDExyz";
        result = possdk.BarCodePrint(handle, BarCodeSymbology.BarCode_Code128, data_CODE128, data_CODE128.length(), "BasicWidth=3|Height=60");
        possdk.FeedLines(handle, 1);

        possdk.PrintText(handle, "QR\n", "");
        String data_QR = "0123456789ABCDEFGHIJKLmnopqrstuvwxyz汉字";
        try {
            result = possdk.BarCodePrint(handle, BarCodeSymbology.BarCode_QR, data_QR, data_QR.getBytes("GB18030").length, "");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        possdk.FeedLines(handle, 1);

        possdk.PrintText(handle, "PDF417\n", "");
        String data_PDF417 = "0123456789ABCDEFGHIJKLmnopqrstuvwxyz汉字";
        try {
            result = possdk.BarCodePrint(handle, BarCodeSymbology.BarCode_PDF417, data_PDF417, data_PDF417.getBytes("GB18030").length, "");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        possdk.FeedLines(handle, 1);

        possdk.PrintText(handle, "MAXICODE\n", "");
        String data_MAXICODE = "0123456789ABCDEFGHIJKLmnopqrstuvwxyz";
        result = possdk.BarCodePrint(handle, BarCodeSymbology.BarCode_MAXICODE, data_MAXICODE, data_MAXICODE.length(), "");
        possdk.FeedLines(handle, 1);

        possdk.PrintText(handle, "GS1\n", "");
        String data_GS1 = "01234567891234";
        result = possdk.BarCodePrint(handle, BarCodeSymbology.BarCode_GS1, data_GS1, data_GS1.length(), "GS1Type=1");
        possdk.FeedLines(handle, 1);

        result = possdk.PaperCut(handle, 0, 0);

        return result;
    }

    public int Sample_Restaurant(byte[] errorStatus,int[] statusLength)
    {
        int nRet = SUCCESS;
        int nStatus = 0;

        nStatus = possdk.QueryStatus(handle, errorStatus,statusLength);
        String status = new String(errorStatus,0,statusLength[0]);
        if (!status.contains("Normal")) {
            return nStatus;
        }

        String lineBetwen = "------------------------------------------------\n";

        nRet = possdk.PrintText(handle, lineBetwen, "Bold=0|Align=0|HScale=1|VScale=1|Underline=0|Italics=0|Font=FontA");
        nRet = possdk.PrintText(handle,"2020年04月10日09：48\n","");
        nRet = possdk.PrintText(handle, lineBetwen, "");
        nRet = possdk.PrintText(handle, "#20美团外卖\n", "Bold=1|Align=1|HScale=2|VScale=2");
        nRet = possdk.PrintText(handle, "切尔西ChelseaKitchen\n", "Bold=0|HScale=1|VScale=1");
        nRet = possdk.PrintText(handle, "在线支付(已支付)\n", "Bold=1|HScale=2|VScale=2");
        nRet = possdk.PrintText(handle, "订单号：5415221202244734\n", "Bold=0|HScale=1|VScale=1");
        nRet = possdk.PrintText(handle, "下单时间：2021-04-10 10：00：00\n", "");
        nRet = possdk.PrintText(handle, "---------------------1号口袋--------------------\n", "Align=0");
        nRet = possdk.PrintText(handle, "意大利茄汁意面X1                            32.9\n", "");
        nRet = possdk.PrintText(handle, "7寸浓香芝士披萨X1                           34.9\n", "");
        nRet = possdk.PrintText(handle, "葡式蛋挞2个装X1                                9\n", "");
        nRet = possdk.PrintText(handle, "9寸培根土豆披萨X1                           54.9\n", "");
        nRet = possdk.PrintText(handle, "9寸芝士加量X1                                 10\n", "");
        nRet = possdk.PrintText(handle, "---------------------其他-----------------------\n", "");
        nRet = possdk.PrintText(handle, "[满100.0元减40.0元]\n", "");
        nRet = possdk.PrintText(handle, "[减配送费3.0元]\n", "");
        nRet = possdk.PrintText(handle, "\n", "");
        nRet = possdk.PrintText(handle, "餐盒费：7\n", "");
        nRet = possdk.PrintText(handle, "[赠送惠尔康茶饮 X 1]:\n", "");
        nRet = possdk.PrintText(handle, lineBetwen, "");
        nRet = possdk.PrintText(handle, "原价：￥141.7  \n", "Align=2");
        nRet = possdk.PrintText(handle, "实付：￥107.7 \n", "Bold=1|HScale=2|VScale=2");
        nRet = possdk.PrintText(handle, lineBetwen, "Bold=0|Align=0|HScale=1|VScale=1");
        nRet = possdk.PrintText(handle, "通鑫学生公寓A5-2\n", "Bold=1|HScale=2|VScale=2");
        nRet = possdk.PrintText(handle, "号（A5-107）\n", "Bold=0");
        nRet = possdk.PrintText(handle, "131****0501\n", "");
        nRet = possdk.PrintText(handle, "苏（先生）\n", "");
        nRet = possdk.PrintText(handle, lineBetwen, "HScale=1|VScale=1");

        nRet = possdk.PaperCut(handle, 0, 0);

        return nRet;
    }


    public int Sample_Restaurant_En(byte[] errorStatus,int[] statusLength)
    {
        int nRet = SUCCESS;
        int nStatus = 0;

        nStatus = possdk.QueryStatus(handle, errorStatus,statusLength);
        String status = new String(errorStatus,0,statusLength[0]);
        if (!status.contains("Normal")) {
            return nStatus;
        }

        nRet = possdk.PrintText(handle, "XxxxXxxx\n", "Bold=1|Align=1|HScale=1|VScale=1|Underline=0|Italics=0|Font=FontA");
        nRet = possdk.PrintText(handle, "201 East 31st St.\n", "Bold=0");
        nRet = possdk.PrintText(handle, "New York, NY 10000\n", "");
        nRet = possdk.PrintText(handle, "0344590786\n", "");
        nRet = possdk.PrintText(handle, "\n", "");
        nRet = possdk.PrintText(handle, "Server: Kristen                       Station: 7\n", "Align=0");
        nRet = possdk.PrintText(handle, "------------------------------------------------\n", "");
        nRet = possdk.PrintText(handle, "Order #: 123401                          Dine In\n", "");
        nRet = possdk.PrintText(handle, "Table: L6                               Guest: 2\n", "");
        nRet = possdk.PrintText(handle, "------------------------------------------------\n", "");
        nRet = possdk.PrintText(handle, "1 Lamb Embuchado.                          12.00\n", "");
        nRet = possdk.PrintText(handle, "1 NY Strip 6oz                             18.00\n", "");
        nRet = possdk.PrintText(handle, "1 Mozzarella Flatbread                     10.00\n", "");
        nRet = possdk.PrintText(handle, "1 Mahan                                     5.00\n", "");
        nRet = possdk.PrintText(handle, "\n", "");
        nRet = possdk.PrintText(handle, "Bar Subtotal:                               0.00\n", "");
        nRet = possdk.PrintText(handle, "Food Subtotal:                             45.00\n", "");
        nRet = possdk.PrintText(handle, "Tax:                                        3.99\n", "");
        nRet = possdk.PrintText(handle, "                                        ========\n", "");
        nRet = possdk.PrintText(handle, "TOTAL:            $49.00\n", "Bold=1|HScale=2|VScale=2");
        nRet = possdk.PrintText(handle, "\n", "Bold=0|HScale=1|VScale=1");
        nRet = possdk.PrintText(handle, ">> Ticket #: 11 <<\n", "Align=1");
        nRet = possdk.PrintText(handle, "4/23/2019 7:03:24 PM\n", "");
        nRet = possdk.PrintText(handle, "**********************************************\n", "");
        nRet = possdk.PrintText(handle, "Join our mailing list for exclusive offers\n", "");
        nRet = possdk.PrintText(handle, "\n", "");
        nRet = possdk.PrintText(handle, "www.XxxxXxxx.com\n", "Underline=1");
        nRet = possdk.PrintText(handle, "\n", "Underline=0");
        nRet = possdk.PrintText(handle, "15% Gratuity = $6.75\n", "");
        nRet = possdk.PrintText(handle, "18% Gratuity = $8.10\n", "");
        nRet = possdk.PrintText(handle, "20% Gratuity = $9.00\n", "");
        nRet = possdk.PrintText(handle, "22% Gratuity = $9.90\n", "");
        nRet = possdk.PrintText(handle, "\n", "");
        nRet = possdk.PrintText(handle, "**********************************************\n", "");
        nRet = possdk.PrintText(handle, "Join Us For Our $5 Happy Hour Daily 5-8pm\n", "");

        nRet = possdk.PaperCut(handle, 0, 0);
        return nRet;
    }




    public int SampleXML(Context context){
        int nRet = SUCCESS;
        String path = copyAssetGetFilePath("SAMPLELOGO.jpg",context);
        String xmlStr = "<pos-print>"+ "<image align='center'>"+path+"</image>"+
                "<text align='left' bold='false' scale-h='1' scale-v='1' italics='1' font='font_a'>Ginger A   Store   #12345REG   #02&#xA;</text>"+
                "<text>Date:2006-04-09 	       Time:18:30&#xA;</text>"+
                "<text>-----------------------------------------&#xA;</text>"+
                "<text>Item         Qty             Amount&#xA;</text>"+
                "<text>Apple	      1               $1.00&#xA;</text>"+
                "<text>Orange	      2               $5.00&#xA;</text>"+
                "<text>Brush	      1               $3.8&#xA;</text>"+
                "<text>Beer	      2               $8.00&#xA;</text>"+
                "<text>-----------------------------------------&#xA;</text>"+
                "<text>Subtotal:&#xA;</text>"+
                "<text>Tax:&#xA;</text>"+
                "<text scale-h='2' scale-v='2'>Total:       $17.80&#xA;</text>"+
                "<text scale-h='1' scale-v='1'>-----------------------------------------&#xA;</text>"+
                "<text align='center'>Customer signature&#xA;</text>"+
                "<text>Thank you&#xA;</text>"+
                "<text>Welcome next time&#xA;</text>"+
                "<symbol align='center' type='pdf417'>12345REG</symbol>"+
                "<cut/>"+
                "</pos-print>";

        String xmlStr1 ="<pos-print><text>Date:2006-04-09 	       Time:18:30&#xA;</text><cut/></pos-print>";
        nRet = possdk.PrintContent(handle, 0, xmlStr);

        return nRet;
    }


    public int SampleXMLPageMode(Context context)
    {
        int nRet = SUCCESS;
        String path = copyAssetGetFilePath("SAMPLELOGO.jpg",context);
        String xmlStr = "<pos-print>"+
                "<page width='600' height='300' x='0' y='0'>"+
                "<image x='60' y='40'>"+path+"</image>"+
                "<text x='20' y='120' bold='true'>==============================&#xA;</text>"+
                "<text x='20' bold='true'>POS Printer Page Mode Sample.&#xA;</text>"+
                "<text x='60' bold='false' underline='true'>Sample Text UnderLine.&#xA;</text>"+
                "<text x='20' underline='false'>==============================&#xA;</text>"+
                "<feed/>"+
                "<text x='10' italics='false' scale-h='2' scale-v='2'>Thank you for your use.&#xA;</text>"+
                "<symbol type='qr' x='400' y='200' width='5'>SAMPLE PAGE MODE</symbol>"+
                "</page>"+
                "<cut/>"+
                "</pos-print>";

        nRet = possdk.PrintContent(handle, 0, xmlStr);

        Log.e("11111","nRet"+nRet);
        return nRet;
    }
}
