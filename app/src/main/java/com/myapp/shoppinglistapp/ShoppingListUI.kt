package com.myapp.shoppinglistapp

import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


data class ShoppingItem(
    val id: Int,
    var name: String,
    var quantity: Int,
    var isEditing: Boolean = false,
)

@RequiresApi(35)
@Composable
fun ShoppingListAppUI(innerPadding: PaddingValues) {
    var stateItems by remember {
        mutableStateOf(listOf<ShoppingItem>())
    }
    var showDialog by remember {
        mutableStateOf(false)
    }
    var itemName by remember {
        mutableStateOf("")
    }
    var itemQuantity by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .padding(paddingValues = innerPadding)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = { showDialog = true }) {
            Text(text = "Create an Item")
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(stateItems) { item ->
                if (item.isEditing) {
                    ShoppingItemEditor(item = item, onEditComplete = { editedName, editedQuantity ->
                        stateItems = stateItems.map { it.copy(isEditing = false) }
                        val editedItem = stateItems.find { it.id == item.id }
                        editedItem?.let {
                            it.name = editedName
                            it.quantity = editedQuantity
                        }
                    })
                } else {
                    ShoppingListItem(item = item, onEditClick = {
                        stateItems = stateItems.map {
                            // finding out which item we are editing and changing "isEditing boolean" to true
                            it.copy(isEditing = it.id == item.id)
                        }
                    }, onDeleteClick = {
                        stateItems = stateItems - item
                    })
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    ElevatedButton(onClick = {
                        if (itemName.isNotBlank()) {
                            val newItem: ShoppingItem = ShoppingItem(
                                id = stateItems.size + 1,
                                name = itemName,
                                quantity = itemQuantity.toInt(),
                            )
                            stateItems = stateItems + newItem
                            showDialog = false
                            itemName = ""
                            itemQuantity = ""
                        }
                    }) {
                        Text(text = "Add Item")
                    }
                    ElevatedButton(onClick = { showDialog = false }) {
                        Text(text = "Cancel")
                    }
                }
            },
            title = {
                Text(text = "Add Shopping Item")
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { text -> itemName = text },
                        singleLine = true,
                        modifier = Modifier.padding(8.dp),
                        label = { Text(text = "Name") }
                    )
                    OutlinedTextField(
                        value = itemQuantity,
                        onValueChange = { text -> itemQuantity = text },
                        singleLine = true,
                        modifier = Modifier.padding(8.dp),
                        label = { Text(text = "Quantity") },
                    )
                }
            }
        )
    }
}

@Composable
fun ShoppingItemEditor(item: ShoppingItem, onEditComplete: (String, Int) -> Unit) {
    var editedName by remember {
        mutableStateOf(item.name)
    }
    var editedQuantity by remember {
        mutableStateOf(item.quantity.toString())
    }
    var isEditing by remember {
        mutableStateOf(item.isEditing)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Column {
            BasicTextField(
                value = editedName,
                onValueChange = { text -> editedName = text },
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp),
            )

            BasicTextField(
                value = editedQuantity,
                onValueChange = { text -> editedQuantity = text },
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp),
            )
        }

        Button(onClick = {
            isEditing = false
            onEditComplete(editedName, editedQuantity.toIntOrNull() ?: 1)
        }) {
            Text(text = "Save")
        }


    }
}

@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(
                border = BorderStroke(2.dp, Color(0xff018786)),
                shape = RoundedCornerShape(20),
            )
    ) {
        Text(text = item.name, modifier = Modifier.padding(8.dp))
        Text(text = "Qty: ${item.quantity}", modifier = Modifier.padding(8.dp))

        Row(modifier = Modifier.padding(8.dp)) {
            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Icon")
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Icon")
            }
        }
    }
}
