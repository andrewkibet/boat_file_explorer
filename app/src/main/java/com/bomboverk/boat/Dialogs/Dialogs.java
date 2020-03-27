package com.bomboverk.boat.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.bomboverk.boat.ExplorerActivity;
import com.bomboverk.boat.ItensAdapter.Itens;
import com.bomboverk.boat.R;
import com.bomboverk.boat.StorageHelpers.StorageHelper;

import java.util.ArrayList;

public class Dialogs {

    private ExplorerActivity context;
    private StorageHelper storageHelper;

    private SparseBooleanArray selectedItens = new SparseBooleanArray();
    private ArrayList<Itens> itens = new ArrayList<>();

    public Dialogs(ExplorerActivity context) {
        this.context = context;
        storageHelper = new StorageHelper(this, context);
    }

    public void createRename(final SparseBooleanArray selectedItens, final ArrayList<Itens> itens) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_rename);

        Button btnCreate = dialog.findViewById(R.id.dg_btn_rename);
        Button btnCancel = dialog.findViewById(R.id.dg_btn_cancel);

        final EditText editRename = dialog.findViewById(R.id.dg_input_renamed);
        editRename.setText(itens.get(selectedItens.keyAt(0)).getNome());

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (storageHelper.renameFileAchive(itens.get(selectedItens.keyAt(0)).getDoc(), editRename.getText().toString())) {
                    context.clearRecycle(context.getLastFile());
                }
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setTitle("RENAME");
        dialog.setCancelable(false);
        dialog.show();
    }

    public void createDelete(String titulo, String mensagem, final SparseBooleanArray selectedItens, final ArrayList<Itens> itens) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(mensagem)
                .setTitle(titulo)
                .setPositiveButton(context.getString(R.string.general_05), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (context.getTypeData().equals("notype")){
                            storageHelper.deleteFileFolder(selectedItens, itens);
                        }else{
                            storageHelper.deleteFileArchive(selectedItens, itens);
                        }
                    }
                })
                .setNegativeButton(context.getString(R.string.general_03), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.create();
        builder.show();
    }

    public void createNewFile() {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_create_file_folder);

        Button btnCreate = dialog.findViewById(R.id.dialog_cnff_create);
        Button btnCancel = dialog.findViewById(R.id.dialog_cnff_cancel);

        final EditText editText = dialog.findViewById(R.id.dialog_cnff_editName);

        final RadioGroup radioGroup = dialog.findViewById(R.id.dialog_radio_group);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a = radioGroup.getCheckedRadioButtonId();
                switch (a) {
                    case R.id.arquivo:
                        if (storageHelper.createFileFolder(false, editText.getText().toString(), context.getLastFile()).exists()) {
                            context.createSnack(context.getString(R.string.class_dialogs_create_success));
                            context.clearRecycle(context.getLastFile());
                        }else{
                            context.createSnack(context.getString(R.string.class_dialogs_create_error));
                        }
                        break;
                    case R.id.pasta:
                        if (storageHelper.createFileFolder(true, editText.getText().toString(), context.getLastFile()).exists()) {
                            context.createSnack(context.getString(R.string.class_dialogs_create_success));
                            context.clearRecycle(context.getLastFile());
                        }else{
                            context.createSnack(context.getString(R.string.class_dialogs_create_error));
                        }
                        break;
                }
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setTitle("CREATE");
        dialog.setCancelable(false);
        dialog.show();
    }

    public void createUniversalInfo(String titulo, String mensagem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(mensagem)
                .setTitle(titulo)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.create();
        builder.show();
    }

    public void createCopy(String titulo, String mensagem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(mensagem)
                .setTitle(titulo)
                .setPositiveButton(context.getString(R.string.general_06), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        storageHelper.copyFileFolder(selectedItens, itens);
                    }
                }).setNegativeButton(context.getString(R.string.general_03), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create();
        builder.show();
    }

    public void createCut(String titulo, String mensagem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(mensagem)
                .setTitle(titulo)
                .setPositiveButton(context.getString(R.string.class_dialogs_cutout), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        storageHelper.cutFileFolder(selectedItens, itens);
                    }
                }).setNegativeButton(context.getString(R.string.general_03), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create();
        builder.show();
    }

    public void createCompact() {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_create_zip);

        Button btnCreate = dialog.findViewById(R.id.dialog_cnz_create);
        Button btnCancel = dialog.findViewById(R.id.dialog_cnz_cancel);

        final EditText editRename = dialog.findViewById(R.id.dialog_cnz_editName);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storageHelper.compressFileFolder(selectedItens, itens, editRename.getText().toString());
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setTitle("RENAME");
        dialog.setCancelable(false);
        dialog.show();
    }

    public ExplorerActivity getExActivity() {
        return context;
    }

    public void updateContent() {
        context.clearRecycle(context.getLastFile());
    }

    public SparseBooleanArray getSelectedItens() {
        return selectedItens;
    }

    public void setSelectedItens(SparseBooleanArray selectedItens) {
        this.selectedItens = selectedItens.clone();
    }

    public ArrayList<Itens> getItens() {
        return itens;
    }

    public void setItens(ArrayList<Itens> itens) {
        this.itens.clear();
        this.itens.addAll(itens);
    }
}
