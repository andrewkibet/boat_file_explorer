package com.bomboverk.boat.StorageHelpers;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.documentfile.provider.DocumentFile;

import com.bomboverk.boat.Dialogs.Dialogs;
import com.bomboverk.boat.ItensAdapter.Itens;
import com.bomboverk.boat.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class StorageHelper {

    Dialogs context;
    Context contexts;

    int[] fileIcons = {R.drawable.ic_file_unknown,
            R.drawable.ic_file_audio,
            R.drawable.ic_file_exel,
            R.drawable.ic_file_image,
            R.drawable.ic_file_powerpoint,
            R.drawable.ic_file_video,
            R.drawable.ic_file_word,
            R.drawable.ic_file_zip,
            R.drawable.ic_file_folder,
            R.drawable.ic_file_pdf,
            R.drawable.ic_file_applications,
            R.drawable.ic_file_text};

    public StorageHelper(Dialogs context, Context contexts) {
        this.context = context;
        this.contexts = contexts;
    }

    public void deleteFileFolder(SparseBooleanArray selectedItens, ArrayList<Itens> itens) {
        ProgressesAsync async = new ProgressesAsync();
        ProgressItens progressItens = new ProgressItens(selectedItens, itens);
        progressItens.setType(0);// DELETE
        async.execute(progressItens);
    }

    public void copyFileFolder(SparseBooleanArray selectedItens, ArrayList<Itens> itens) {
        ProgressesAsync async = new ProgressesAsync();
        ProgressItens progressItens = new ProgressItens(selectedItens, itens);
        progressItens.setType(1); //COPY
        async.execute(progressItens);
    }

    public void cutFileFolder(SparseBooleanArray selectedItens, ArrayList<Itens> itens) {
        ProgressesAsync async = new ProgressesAsync();
        ProgressItens progressItens = new ProgressItens(selectedItens, itens);
        progressItens.setType(2); //CUT
        async.execute(progressItens);
    }

    public void compressFileFolder(SparseBooleanArray selectedItens, ArrayList<Itens> itens, String nomeZip) {
        ProgressesAsync async = new ProgressesAsync();
        ProgressItens progressItens = new ProgressItens(selectedItens, itens);
        progressItens.setType(3); //COMPRESS
        progressItens.setZipName(nomeZip);
        async.execute(progressItens);
    }

    public void deleteFileArchive(SparseBooleanArray selectedItens, ArrayList<Itens> itens) {
        ProgressesAsync async = new ProgressesAsync();
        ProgressItens progressItens = new ProgressItens(selectedItens, itens);
        progressItens.setType(4);// DELETE COntent resolver
        async.execute(progressItens);
    }

    public DocumentFile createFileFolder(boolean isFolder, String nome, DocumentFile file) {

        DocumentFile docCreated = null;

        if (isFolder) {
            docCreated = file.createDirectory(nome);
        } else {
            docCreated = file.createFile("*/*", nome);
        }

        return docCreated;
    }

    private DocumentFile createZip(String fileName) {

        DocumentFile docCreated = null;
        docCreated = context.getExActivity().getLastFile().createFile("application/zip", fileName);

        return docCreated;
    }

    public boolean renameFileAchive(DocumentFile file, String newName) {

        boolean rename = file.renameTo(newName);

        if (rename){
            return true;
        }else{
            return false;
        }

        //DocumentsContract.renameDocument(contexts.getContentResolver(), file.getUri(), newName);
    }

    public String getFolderSizeLabel(DocumentFile file) {
        long size = getFolderSize(file) / 1024; // Get size and convert bytes into Kb.
        if (size >= 1024) {
            return (size / 1024) + " MB";
        } else {
            return size + " KB";
        }
    }

    public long getFolderSize(DocumentFile file) {
        long size = 0;
        if (file.isDirectory()) {
            for (DocumentFile child : file.listFiles()) {
                size += getFolderSize(child);
            }
        } else {
            size = file.length();
        }
        return size;
    }

    public String getExt(String filePath) {
        int strLength = filePath.lastIndexOf(".");
        if (strLength > 0)
            return filePath.substring(strLength + 1).toLowerCase();
        return null;
    }

    /*public String getFileFolderName(String filePath) {
        String strLength = filePath.substring(0, filePath.lastIndexOf("."));
        return strLength;
    }*/

    public int getFileIcon(String extension) {

        int e = fileIcons[0];

        if (extension.equals("folder")) {
            e = fileIcons[8];
        } else if (extension.equals("zip") || extension.equals("rar")) {
            e = fileIcons[7];
        } else if (extension.equals("mp3") || extension.equals("wav")) {
            e = fileIcons[1];
        } else if (extension.equals("xltm") || extension.equals("xls") || extension.equals("xlt")) {
            e = fileIcons[2];
        } else if (extension.equals("png") || extension.equals("jpeg") || extension.equals("gif") || extension.equals("jpg")) {
            e = fileIcons[3];
        } else if (extension.equals("pptx") || extension.equals("pptm") || extension.equals("ppt")) {
            e = fileIcons[4];
        } else if (extension.equals("mp4") || extension.equals("avi") || extension.equals("mov") || extension.equals("wmv")) {
            e = fileIcons[5];
        } else if (extension.equals("doc") || extension.equals("docm") || extension.equals("docx")) {
            e = fileIcons[6];
        } else if (extension.equals("pdf")) {
            e = fileIcons[9];
        } else if (extension.equals("apk")) {
            e = fileIcons[10];
        }else if (extension.equals("txt")){
            e = fileIcons[11];
        }

        return e;
    }

    public String formatWay(String way) {
        DocumentFile pickedDir = DocumentFile.fromTreeUri(contexts, Uri.parse(way));
        String docId = DocumentsContract.getDocumentId(pickedDir.getUri());
        return docId.replaceAll(":", "/");
    }

    public Dialog createDialogProgress() {
        final Dialog dialog = new Dialog(contexts);
        dialog.setContentView(R.layout.dialog_progresses);
        dialog.setTitle("Info");
        dialog.setCancelable(false);
        return dialog;
    }

    private class ProgressesAsync extends AsyncTask<ProgressItens, Integer, Integer[]> {

        int type;
        Dialog dialog = createDialogProgress();

        LinearLayout principal = dialog.findViewById(R.id.linearProgress);
        TextView infoDialog = (TextView) dialog.findViewById(R.id.diag_progress_info);
        Button btnCancel = dialog.findViewById(R.id.diag_progress_btn_cancel);
        ProgressBar progressBar = dialog.findViewById(R.id.diag_progress_barinfo);

        LinearLayout feedbakcl = dialog.findViewById(R.id.linearFinishInfos);
        TextView totalText = dialog.findViewById(R.id.diag_progress_totalfiles);
        TextView successText = dialog.findViewById(R.id.diag_progress_successText);
        TextView errosText = dialog.findViewById(R.id.diag_progress_errorText);
        Button btnOk = dialog.findViewById(R.id.diag_progress_btn_close);

        @Override
        protected Integer[] doInBackground(ProgressItens... progressItens) {
            int progresso = 0;
            int success = 0;
            int erros = 0;

            ArrayList<Itens> item = progressItens[0].getItem();
            SparseBooleanArray selectedItens = progressItens[0].getSelectedItens();

            type = progressItens[0].getType();
            if (type == 0 || type == 4) {
                for (int i = 0; i < selectedItens.size(); i++) {
                    if (!isCancelled()) {
                        if (type == 0){

                            boolean deleted = item.get(selectedItens.keyAt(i)).getDoc().delete();

                            if (deleted) {
                                success++;
                            } else {
                                erros++;
                            }
                        }else {
                            contexts.getContentResolver().delete(Uri.parse(item.get(selectedItens.keyAt(i)).getUrl()), null, null);
                            success++;
                        }
                        progresso++;
                        publishProgress(progresso, selectedItens.size());
                    }
                }
                return new Integer[]{success, erros, type};
            } else if (type == 1 || type == 2) {
                for (int i = 0; i < selectedItens.size(); i++) {
                    if (!isCancelled()) {
                        DocumentFile createDocumentToCopy = null;

                        if (item.get(selectedItens.keyAt(i)).getDoc().isFile()) {
                            createDocumentToCopy = createFileFolder(false, item.get(selectedItens.keyAt(i)).getDoc().getName(), context.getExActivity().getLastFile());
                        } else {
                            createDocumentToCopy = createFileFolder(true, item.get(selectedItens.keyAt(i)).getDoc().getName(), context.getExActivity().getLastFile());
                        }

                        if (copyFilesAndFolderSystemContent(item.get(selectedItens.keyAt(i)).getDoc(), createDocumentToCopy, true)) {

                            if (type == 2) {

                                try {
                                    boolean deleted = DocumentsContract.deleteDocument(contexts.getContentResolver(), item.get(selectedItens.keyAt(i)).getDoc().getUri());

                                    if (deleted) {
                                        success++;
                                    } else {
                                        erros++;
                                    }

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                success++;
                            }

                        } else {
                            erros++;
                        }

                        progresso++;
                        publishProgress(progresso, selectedItens.size());
                    }
                }
                return new Integer[]{success, erros, type};
            } else {
                try {
                    OutputStream dest = contexts.getContentResolver().openOutputStream(createZip(progressItens[0].getZipName()).getUri());
                    ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

                    for (int i = 0; i < selectedItens.size(); i++) {
                        if (!isCancelled()) {
                            zipFile(item.get(selectedItens.keyAt(i)).getDoc(), item.get(selectedItens.keyAt(i)).getDoc().getName(), out);
                            success++;
                            progresso++;
                            publishProgress(progresso, selectedItens.size());
                        }
                    }
                    out.close();
                } catch (Exception e) {
                    erros++;
                    e.printStackTrace();
                }
                return new Integer[]{success, erros, type};
            }
        }

        @Override
        protected void onPreExecute() {
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getStatus() == Status.RUNNING) {
                        cancel(true);
                    }
                    dialog.dismiss();
                }
            });

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer[] integers) {
            int total = integers[0] + integers[1];

            totalText.setText(contexts.getString(R.string.diag_p_totalfiles) + " " + total);

            if (integers[2] == 0) {
                successText.setText(contexts.getString(R.string.diag_p_success_removed) + " " + integers[0]);
                errosText.setText(contexts.getString(R.string.diag_p_error_removed) + " " + integers[1]);
            } else if (integers[2] == 1) {
                successText.setText(contexts.getString(R.string.diag_p_success_copy) + " " + integers[0]);
                errosText.setText(contexts.getString(R.string.diag_p_error_copy) + " " + integers[1]);
            } else if (integers[2] == 2) {
                successText.setText( contexts.getString(R.string.diag_p_success_moved) + " " + integers[0]);
                errosText.setText( contexts.getString(R.string.diag_p_error_moved) + " " + integers[1]);
            } else if (integers[2] == 3) {
                successText.setText( contexts.getString(R.string.diag_p_success_compress) + " " + integers[0]);
                errosText.setText( contexts.getString(R.string.diag_p_error_compress) + " " + integers[1]);
            } else {
                successText.setText(contexts.getString(R.string.diag_p_success_removed) + " " + integers[0]);
                errosText.setText(contexts.getString(R.string.diag_p_error_removed) + " " + integers[1]);
            }

            principal.setVisibility(View.INVISIBLE);
            feedbakcl.setVisibility(View.VISIBLE);

            if (type == 1 || type == 2) {
                context.getExActivity().prepareToCopy(false);
            }
            context.updateContent();
            super.onPostExecute(integers);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setMax(values[1]);
            progressBar.setProgress(values[0]);
            infoDialog.setText(values[0] + " " + contexts.getString(R.string.diag_p_outof) + " " + values[1]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
            if (type == 1) {
                context.getExActivity().prepareToCopy(false);
            }
            context.updateContent();
            super.onCancelled();
        }

        private boolean copyFilesAndFolderSystemContent(DocumentFile currentDoc, DocumentFile targetDoc, boolean rootpah) {
            try {
                if (currentDoc.isFile()) {
                    if (!rootpah) {
                        createFileFolder(false, currentDoc.getName(), targetDoc);
                    }
                    InputStream in = contexts.getContentResolver().openInputStream(currentDoc.getUri());
                    OutputStream out = contexts.getContentResolver().openOutputStream(targetDoc.getUri());
                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    in.close();
                    out.flush();
                    out.close();
                } else {
                    if (currentDoc.listFiles().length == 0) {
                        if (!rootpah) {
                            createFileFolder(true, currentDoc.getName(), targetDoc);
                        }
                    } else {
                        for (DocumentFile fc : currentDoc.listFiles()) {
                            if (!isCancelled()) {
                                DocumentFile created = createFileFolder(true, fc.getName(), targetDoc);
                                copyFilesAndFolderSystemContent(fc, created, false);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (targetDoc.length() == currentDoc.length()) {
                return true;
            } else {
                return false;
            }
        }

        private void zipFile(DocumentFile fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
            if (fileToZip.isDirectory()) {
                if (fileName.endsWith("/")) {
                    zipOut.putNextEntry(new ZipEntry(fileName));
                    zipOut.closeEntry();
                } else {
                    zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                    zipOut.closeEntry();
                }
                for (DocumentFile childFile : fileToZip.listFiles()) {
                    zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
                }
                return;
            }
            InputStream fis = contexts.getContentResolver().openInputStream(fileToZip.getUri());
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }

    }
}
