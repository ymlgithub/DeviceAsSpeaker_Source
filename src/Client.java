import javax.sound.sampled.*;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

//adb forward tcp:9500 tcp:9500
public class Client {
    public static void main(String[] args) throws IOException, InterruptedException, LineUnavailableException {
        Socket socket = new Socket("localhost", 9500);
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
        System.out.println("已连接");
        byte[] buffer = new byte[16];
        int size = inputStream.read(buffer);
        String bs = new String(buffer, 0, size, StandardCharsets.UTF_8);
        int bufferSize = Integer.parseInt(bs);
        System.out.println("buffer size : " + bufferSize);
        buffer = new byte[bufferSize];

        AudioFormat audioFormat = getAudioFormat();
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
        TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
        targetDataLine.open();
        targetDataLine.start();

        while ( (size = targetDataLine.read(buffer, 0 , bufferSize)) > 0) {
            outputStream.write(buffer, 0, size);
            outputStream.flush();
        }
        System.out.println(size);

        Thread.sleep(10000);
        inputStream.close();
        outputStream.close();
    }

    private static AudioFormat getAudioFormat(){
        float sampleRate = 48000;
        int sampleSizeInBits = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate,sampleSizeInBits,channels,signed,bigEndian);
    }
}
