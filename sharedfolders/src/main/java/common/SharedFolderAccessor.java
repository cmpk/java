package common;

import java.util.ArrayList;
import java.util.List;

/**
 * Windows 共有フォルダにアクセスするための操作を提供する.
 */
public class SharedFolderAccessor {
    private final String stdoutCharset;

    /**
     * コンストラクタ.
     * @param charset @see Command#Command(String[], String)
     */
    public SharedFolderAccessor(final String charset) {
        this.stdoutCharset = charset;
    }

    /**
     * 利用可能なドライブ文字列を検索する.
     * Zから降順に検索し、最初に見つかった文字列を返す.
     *
     * @param outputs 標準出力に出力された内容を格納するインスタンス.
     * @return 利用可能なドライブ文字列.
     */
    public String searchDriveLetter(List<String> outputs) { //SUPPRESS CHECKSTYLE outputs needs to be writable
        outputs = (outputs == null) ? new ArrayList<String>() : outputs;
        char c = 'Z';
        for (int i = 0; i < 26; i++) { //SUPPRESS CHECKSTYLE ignore magic number
            String[] commandList = {"if not exist " + c + ":\\ echo " + c};
            List<String> out = new ArrayList<String>();
            try {
                Command.run("./", commandList, this.stdoutCharset, out);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (out.size() > 0) {
                // 出力内容を呼び出し元に返すための引数に追加する
                outputs.addAll(out);
            }
            if (out.size() == 1 && out.get(0).length() == 1) {
                return outputs.get(0);
            }
            c--;
        }
        return "";
    }

    /**
     * 共有フォルダをネットワークドライブに割り当てる.
     * これにより、共有フォルダ上のファイルを操作可能になる.
     * @param driveLetter 割り当てるドライブ文字列
     * @param sharedDirPath 共有フォルダのパス
     * @param userId 共有フォルダを利用する際のユーザID
     * @param password パスワード
     * @param outputs 標準出力に出力された内容を格納するインスタンス.
     * @return 成功した場合は true
     * @throws CommandException
     */
    public boolean assignNetworkDrive(final String driveLetter, final String sharedDirPath, final String userId, final String password, List<String> outputs) throws CommandException { //SUPPRESS CHECKSTYLE outputs needs to be writable
        String[] commandList = {"net use", driveLetter + ":", sharedDirPath, password, "/USER:" + userId};
        outputs = (outputs == null) ? new ArrayList<String>() : outputs;
        int exitCode = Command.run("./", commandList, this.stdoutCharset, outputs);

        if (exitCode == 0) {
            return true;
        }

        return false;
    }

    /**
     * ネットワークドライブを切断する.
     * @param driveLetter 切断対象のドライブ文字列
     * @param outputs 標準出力に出力された内容を格納するインスタンス.
     * @return 成功した場合は true
     * @throws CommandException
     */
    public boolean deleteNetworkDrive(final String driveLetter, List<String> outputs) throws CommandException { //SUPPRESS CHECKSTYLE outputs needs to be writable
        String[] commandList = {"net use", driveLetter + ":", "/delete"};
        outputs = (outputs == null) ? new ArrayList<String>() : outputs;
        int exitCode = Command.run("./", commandList, this.stdoutCharset, outputs);

        if (exitCode == 0) {
            return true;
        }

        return false;
    }
}
