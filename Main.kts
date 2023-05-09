import java.io.File
import java.util.concurrent.Executors

internal object Constants {
    // template placeholder to replace a string in commands
    internal const val PLACEHOLDER = "TEMPLATE_PACKAGE_NAME"

    // utility commands
    private const val COMMAND_CHMOD_MOUNT = "chmod +x"
    internal const val COMMAND_PID_OF = "pidof -s"
    internal const val COMMAND_CREATE_DIR = "mkdir -p"
    internal const val COMMAND_LOGCAT = "logcat -c && logcat | grep AndroidRuntime"
    internal const val COMMAND_RESTART = "pm resolve-activity --brief $PLACEHOLDER | tail -n 1 | xargs am start -n && kill ${'$'}($COMMAND_PID_OF $PLACEHOLDER)"

    // default mount file name
    private const val NAME_MOUNT_SCRIPT = "mount_revanced_$PLACEHOLDER.sh"

    // initial directory to push files to via adb push
    /*
    internal const val PATH_INIT_PUSH = "/data/local/tmp/revanced.delete"
    */
    internal const val PATH_INIT_PUSH = "/data/local/tmp/ATempFile"

    // revanced path
    internal const val PATH_REVANCED = "/data/adb/revanced/"

    // revanced apk path
    internal const val PATH_REVANCED_APP = "$PATH_REVANCED$PLACEHOLDER.apk"

    // delete command
    internal const val COMMAND_DELETE = "rm -rf $PLACEHOLDER"

    // mount script path
    internal const val PATH_MOUNT = "/data/adb/service.d/$NAME_MOUNT_SCRIPT"

    // move to revanced apk path & set permissions
    internal const val COMMAND_PREPARE_MOUNT_APK =
        "base_path=\"$PATH_REVANCED_APP\" && mv $PATH_INIT_PUSH ${'$'}base_path && chmod 644 ${'$'}base_path && chown system:system ${'$'}base_path && chcon u:object_r:apk_data_file:s0  ${'$'}base_path"

    // unmount command
    internal const val COMMAND_UMOUNT =
        "grep $PLACEHOLDER /proc/mounts | while read -r line; do echo ${'$'}line | cut -d \" \" -f 2 | sed 's/apk.*/apk/' | xargs -r umount -l; done"

    // install mount script & set permissions
    internal const val COMMAND_INSTALL_MOUNT = "mv $PATH_INIT_PUSH $PATH_MOUNT && $COMMAND_CHMOD_MOUNT $PATH_MOUNT"

    // mount script
    internal val CONTENT_MOUNT_SCRIPT =
        """
            #!/system/bin/sh
            MAGISKTMP="${'$'}(magisk --path)" || MAGISKTMP=/sbin
            MIRROR="${'$'}MAGISKTMP/.magisk/mirror"
            while [ "${'$'}(getprop sys.boot_completed | tr -d '\r')" != "1" ]; do sleep 1; done
            
            base_path="$PATH_REVANCED_APP"
            stock_path=${'$'}( pm path $PLACEHOLDER | grep base | sed 's/package://g' )

            chcon u:object_r:apk_data_file:s0  ${'$'}base_path
            mount -o bind ${'$'}MIRROR${'$'}base_path ${'$'}stock_path
        """.trimIndent()
}

internal class device() {
    public fun copy(szDest: String, szLocalFile: String): Unit {
        println("adb push ${szLocalFile} ${szDest}");
    }

    /* MagiskSU. */
    public fun run(szCommand: String): Unit {
        // println("adb shell su --command \"${szCommand}\"");
        println("${szCommand}");
    }

    public fun createFile(szDest: String, szContent: String, szLocalFile: String): Unit {
        /* We cannot create a file since the string contains quote marks, hence it will take much more effort of dealing with them. We can, but the EOL characters... */
        println("---- CREATE A LOCAL FILE WITH THE GIVEN NAME ${szLocalFile} ----");
        println("---- SAVE THE CONTENT BETWEEN THESE TAGS IN THE FILE START ----");
        println("${szContent}");
        println("---- SAVE THE CONTENT BETWEEN THESE TAGS IN THE FILE END ----\n");
        copy(szDest, szLocalFile);
    }

}

@Suppress("UNUSED_PARAMETER")
internal class Adb(private val file: File, private val packageName: String, deviceName: String, private val modeInstall: Boolean = false, private val logging: Boolean = true) {
    private fun String.replacePlaceholder(with: String? = null): String { return this.replace(Constants.PLACEHOLDER, with ?: packageName); }
    private fun String.printStart(with: String): Unit { println("# ${with} start:\n"); }
    private fun String.printEnd(with: String): Unit { println("\n# ${with} end.\n"); }
    // private fun String.printStart(): Unit { println("${this} start:\n"); }
    // private fun String.printEnd(): Unit { println("\n${this} end."); }

    private val device = device();
    internal fun deploy() {
        // println("rm -rf /data/adb/service.d");
        // println("rm -rf /data/adb/post-fs-data.d");
        // println("rm -rf /data/adb/revanced");
        // println("reboot");

        // push patched file
        val szTempString = "ATestFile.sh";
        szTempString.printStart("Push patched file");
        device.copy(Constants.PATH_INIT_PUSH, file.getName())
        szTempString.printEnd("Push patched file");

        // create revanced folder path
        szTempString.printStart("Create revanced folder path");
        device.run("${Constants.COMMAND_CREATE_DIR} ${Constants.PATH_REVANCED}")
        szTempString.printEnd("Create revanced folder path");

        // prepare mounting the apk
        szTempString.printStart("Prepare mounting the apk");
        device.run(Constants.COMMAND_PREPARE_MOUNT_APK.replacePlaceholder())
        szTempString.printEnd("Prepare mounting the apk");

        // push mount script
        szTempString.printStart("Push mount script");
        device.createFile(
           Constants.PATH_INIT_PUSH,
           Constants.CONTENT_MOUNT_SCRIPT.replacePlaceholder()
           , szTempString
        )
        szTempString.printEnd("Push mount script");

        // install mount script
        szTempString.printStart("Install mount script");
        device.run(Constants.COMMAND_INSTALL_MOUNT.replacePlaceholder())
        szTempString.printEnd("Install mount script");

        // unmount the apk for sanity
        szTempString.printStart("Unmount the apk for sanity");
        device.run(Constants.COMMAND_UMOUNT.replacePlaceholder())
        szTempString.printEnd("Unmount the apk for sanity");

        // mount the apk
        szTempString.printStart("Mount the apk");
        device.run(Constants.PATH_MOUNT.replacePlaceholder())
        szTempString.printEnd("Mount the apk");

        // relaunch app
        szTempString.printStart("Relaunch app");
        device.run(Constants.COMMAND_RESTART.replacePlaceholder())
        szTempString.printEnd("Relaunch app");

        // log the app
        println();
    }

    internal fun uninstall() {
        val szTempString = "";
        szTempString.printStart("Uninstalling by unmounting");
        /*
        logger.info("Uninstalling by unmounting")
        */

        // unmount the apk
        szTempString.printStart("Unmount the apk");
        device.run(Constants.COMMAND_UMOUNT.replacePlaceholder())
        szTempString.printEnd("Unmount the apk");

        // delete revanced app
        szTempString.printStart("Delete revanced app");
        device.run(Constants.COMMAND_DELETE.replacePlaceholder(Constants.PATH_REVANCED_APP).replacePlaceholder())
        szTempString.printEnd("Delete revanced app");

        // delete mount script
        szTempString.printStart("Delete mount script");
        device.run(Constants.COMMAND_DELETE.replacePlaceholder(Constants.PATH_MOUNT).replacePlaceholder())
        szTempString.printEnd("Delete mount script");

        /*
        logger.info("Finished uninstalling")
        */
        szTempString.printEnd("Uninstalling by unmounting");
    }
}

fun main() {
    val outputFile = File("rvr2.apk"); /* The patched and unsigned apk file. */
    val packageName = "com.google.android.youtube";
    val adb: Adb = Adb(outputFile, packageName, "", false, false);
    adb.uninstall();
    adb.deploy();
}