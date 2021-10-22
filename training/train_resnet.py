import pandas as pd
import numpy as np
# import tsfresh
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import classification_report
import tensorflow as tf

import os

import matplotlib.pyplot as plt

# keras goodies
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Activation, Flatten, Conv1D, Dropout, MaxPooling1D, BatchNormalization
from tensorflow.keras.layers import LSTM, SimpleRNN, Bidirectional, GRU, Dropout, Input, AveragePooling1D

from tensorflow.keras import optimizers
from tensorflow.keras import regularizers
from tensorflow.keras import metrics as kmetrics
import tensorflow.keras.backend as K
from resconv import identity_block, convolutional_block
from tensorflow.keras.models import Model
# Transfer the tensorflow model to tflite model:
import os
import tensorflow as tf
from tensorflow import keras
import numpy as np
import matplotlib.pyplot as plt
from tensorflow.keras.layers import Flatten
from tensorflow.keras.layers import Dense
from tensorflow.keras.losses import SparseCategoricalCrossentropy
from sklearn.metrics import accuracy_score
print(tf.__version__)
def get_file_size(file_path):
    size = os.path.getsize(file_path)
    return size

def convert_bytes(size, unit=None):
    if unit == "KB":
        return print('File size: ' + str(round(size / 1024, 3)) + ' Kilobytes')
    elif unit == "MB":
        return print('File size: ' + str(round(size / (1024 * 1024), 3)) + ' Megabytes')
    else:
        return print('File size: ' + str(size) + ' bytes')

base_df = pd.DataFrame()
base_df_test = pd.DataFrame()

# clean_data_folder = "./Data/Clean"

# train_data_folder = "./data/train"
train_data_folder = "./Data/Clean"
test_data_folder = "./data/test"

for filename in os.listdir(train_data_folder):
    full_path = f"{train_data_folder}/{filename}"
    print(full_path)
    
    # load data into a DataFrame
    new_df = pd.read_csv(full_path)
    
    # merge into the base DataFrame
    base_df = pd.concat([base_df, new_df])


base_df.reset_index(drop=True, inplace=True)

for filename in os.listdir(test_data_folder):
    full_path_test = f"{test_data_folder}/{filename}"
    print(full_path)
    
    # load data into a DataFrame
    new_df_test = pd.read_csv(full_path_test)
    
    # merge into the base DataFrame
    base_df_test = pd.concat([base_df_test, new_df_test])


base_df_test.reset_index(drop=True, inplace=True)

print(f"The data was collected using the sensors: {base_df.sensor_type.unique()}")
print(f"The data was collected for the activities: {base_df.activity_type.unique()}")
print(f"The number of unique recordings is: {len(base_df.recording_id.unique())}")
print(f"The subject IDs in the recordings are: {len(base_df.subject_id.unique())}")

# example_recording = base_df[base_df.activity_code == 1]
example_recording = base_df


window_size = 100 # 50 datapoints for the window size, which, at 25Hz, means 2 seconds
step_size = 50 # this is 50% overlap

window_number = 0 # start a counter at 0 to keep track of the window number

large_enough_windows = [window for window in example_recording.rolling(window=window_size, min_periods=window_size) if len(window) == window_size]

# we then get the windows with the required overlap

overlapping_windows = large_enough_windows[::step_size] 


# then we will append a window ID to each window
for window in overlapping_windows:
    window.loc[:, 'window_id'] = window_number
    window_number += 1
    
    

# now we concatenate all the resulting windows
final_sliding_windows = pd.concat(overlapping_windows).reset_index(drop=True)

columns_of_interest = ['accel_x', 'accel_y', 'accel_z', 'gyro_x', 'gyro_y', 'gyro_z']

class_labels = {
    
    'Sitting': 0 ,
    'Sitting bent forward': 1,
    'Sitting bent backward': 2 ,
    'Standing': 3,
    'Lying down left': 4,
    'Lying down right': 5 ,
    'Lying down on stomach': 6 ,
    'Lying down on back': 7 ,
    'Walking at normal speed': 8,
    'Running' :9,
    'Climbing stairs':10 ,
    'Descending stairs' :11,
    'Desk work':12,
    'Movement' :13,
    'Falling on knees' :14,
    'Falling on the back': 15,
    'Falling on the left': 16,
    'Falling on the right': 17,
    
}

X = []
y = []

for window_id, group in final_sliding_windows.groupby('window_id'):
    # print(f"window_id = {window_id}")
    
    shape = group[columns_of_interest].values.shape
    # print(f"shape = {shape}")
    
    X.append(group[columns_of_interest].values)
    y.append(class_labels[group["activity_type"].values[0]])
    
X = np.asarray(X)
y = np.asarray(y)

print(f"X shape = {X.shape}")
print(f"y shape = {y.shape}")

X_train = X
y_train = y

# -------------Do the aove again for test data-------------------

example_recording_test = base_df_test

window_number_test = 0 # start a counter at 0 to keep track of the window number

large_enough_windows_test = [window for window in example_recording_test.rolling(window=window_size, min_periods=window_size) if len(window) == window_size]

# we then get the windows with the required overlap

overlapping_windows_test = large_enough_windows_test[::step_size] 


# then we will append a window ID to each window
for window in overlapping_windows_test:
    window.loc[:, 'window_id'] = window_number_test
    window_number_test += 1
    

# now we concatenate all the resulting windows
final_sliding_windows_test = pd.concat(overlapping_windows_test).reset_index(drop=True)


X_test = []
y_test = []

for window_id, group in final_sliding_windows_test.groupby('window_id'):
    # print(f"window_id = {window_id}")
    
    shape = group[columns_of_interest].values.shape
    # print(f"shape = {shape}")
    
    X_test.append(group[columns_of_interest].values)
    y_test.append(class_labels[group["activity_type"].values[0]])
    
X_test = np.asarray(X_test)
y_test = np.asarray(y_test)

print(f"X_test shape = {X_test.shape}")
print(f"y_test shape = {y_test.shape}")



# X_train, X_test, y_train, y_test = train_test_split(X, y,
#                                                     test_size=0.1, train_size=0.9)

# One hot encoded the y labels using pandas function pd.get_dummies()


y_train = np.asarray(pd.get_dummies(y_train), dtype=np.float32)
y_test = np.asarray(pd.get_dummies(y_test), dtype=np.float32)


print(f"X_train shape = {X_train.shape}")
print(f"y_train shape = {y_train.shape}")

print(f"X_test shape = {X_test.shape}")
print(f"y_test shape = {y_test.shape}")


filters = 128
kernel_size = 5
n_features = 6
activation='relu'
n_classes = 18

# dropout = 0.2

#Model 4 bidirectional LSTM model 

N_CLASSES = 18
N_HIDDEN_UNITS = 128

# define model
# model = Sequential()

# model.add(Conv1D(filters=8, kernel_size=kernel_size, activation='linear', input_shape=(window_size, n_features)))
# model.add(BatchNormalization())
# model.add(Activation(activation))

#model.add(Conv1D(filters=16, kernel_size=kernel_size, activation='linear'))
#model.add(BatchNormalization())
#model.add(Activation(activation))

#model.add(Conv1D(filters=32, kernel_size=kernel_size, activation='linear'))
#model.add(BatchNormalization())
#model.add(Activation(activation))
# model.add(ResNet50(input_shape=(window_size, n_features), n_out = n_features))

#--------------new design of model Resnet50+LSTM--------------------------


# input_ = keras.layers.Input(shape=[window_size, n_features])
input_shape = (window_size, n_features)

# Define the input as a tensor with shape input_shape
X_input = Input(input_shape)
# X = MaxPooling1D(10, strides=2)(X_input) 
# X = X_inputs
    
    # Zero-Padding
    # X = ZeroPadding1D(3)(X)
    
    # stage 1, 64 filters, kernel_size=7
# X = Conv1D(64, 3, name = 'conv1')(X)
# X = BatchNormalization()(X)
# X = Activation('relu')(X)
    # X = MaxPooling1D(3, strides=2)(X)

    # Stage 2
X = convolutional_block(X_input, f = 3, filters = [8, 8, 32], stage = 2, block='a', s = 1)
X = identity_block(X, 3, [8, 8, 32], stage=2, block='b')
X = identity_block(X, 3, [8, 8, 32], stage=2, block='c')

    ### START CODE HERE ###
    
X = convolutional_block(X, f = 3, filters = [16,16,64], stage = 3, block='a', s = 2)
X = identity_block(X, 3, [16,16,64], stage=3, block='b')
X = identity_block(X, 3, [16,16,64], stage=3, block='c')
X = identity_block(X, 3, [16,16,64], stage=3, block='d')

    # Stage 4 (≈6 lines)
    
X = convolutional_block(X, f = 3, filters = [32, 32, 128], stage = 4, block='a', s = 2)
X = identity_block(X, 3, [32, 32, 128], stage=4, block='b')
X = identity_block(X, 3, [32, 32, 128], stage=4, block='c')
X = identity_block(X, 3, [32, 32, 128], stage=4, block='d')
X = identity_block(X, 3, [32, 32, 128], stage=4, block='e')
X = identity_block(X, 3, [32, 32, 128], stage=4, block='f')

    # Stage 5 
   
X = convolutional_block(X, f = 3, filters = [32, 32, 128], stage = 5, block='a', s = 2)
X = identity_block(X, 3, [32, 32, 128], stage=5, block='b')
X = identity_block(X, 3, [32, 32, 128], stage=5, block='c')

X = AveragePooling1D(pool_size=5)(X)      
# pool_size=2

# X = Bidirectional(LSTM(64, activation='relu',return_sequences=True, kernel_initializer='he_normal'))(X)
# X = Bidirectional(LSTM(64, activation='relu',return_sequences=True, kernel_initializer='he_normal'))(X)
# X = Bidirectional(LSTM(64, activation='relu',return_sequences=False, kernel_initializer='he_normal'))(X)
    
    # output layer
X = Flatten()(X)
X = Dropout(0.2)(X)
    
    # For regression
X = Dense(N_CLASSES, name='fc-dense', kernel_regularizer=regularizers.l2(0.2), bias_regularizer=regularizers.l2(0.2))(X)
X = Activation('softmax')(X)    
    # for classification, if n_out =1, add:  
    # X = Activation('sigmoid')(X)

    # if n_out > 1, add:  
# X = Activation('softmax')(X)
    
    # Create model
#model = Model(inputs = X_input, outputs = X, name='ResNet50')



#model.add(Bidirectional(LSTM(N_HIDDEN_UNITS, activation='tanh',return_sequences=True, kernel_initializer='he_normal')))  #, input_shape=(window_size, n_features))) #, input_shape=(n_steps,1)))
# model.add(Dropout(0.2))
#model.add(Bidirectional(LSTM(N_HIDDEN_UNITS, activation='tanh',return_sequences=True, kernel_initializer='he_normal' ))) #, input_shape=(n_steps,1)))
# model.add(Dropout(0.2))
#model.add(Bidirectional(LSTM(N_HIDDEN_UNITS, activation='tanh',return_sequences=False, kernel_initializer='he_normal'))) #, input_shape=(n_steps,1)))
# model.add(Dropout(0.2))

# X = Dense(128, activation='relu', kernel_initializer='he_normal')(X)
# X = Dense(N_CLASSES, activation='softmax')(X)
# Create model
model = Model(inputs = X_input, outputs = X, name='ResNet50')

print(model.summary())

# from tensorflow.keras.utils import plot_model
# plot_model(model, to_file='model_plot.png', show_shapes=True, show_layer_names=True)

# model.compile(
#     optimizer=optimizers.SGD(lr=0.01),
#     loss='categorical_crossentropy',
#     metrics = ['accuracy'])

def scheduler1(epoch, lr):
  if epoch < 5:
    return lr
  if epoch < 20:
    return lr * 0.95

  if lr<=1e-7 :
    return 1e-7

  else:
    print("learning rate: ", lr)
    return lr * 0.9

def keep(epoch, lr):
    return lr

def scheduler(epoch, lr):

  if lr<=1e-6 :
    # print("learning rate: ", lr)
    return 1e-6

  else:
    print("learning rate: ", lr)
    return lr * 0.9


def lr_schedule(epoch):
  """
  Returns a custom learning rate that decreases as epochs progress.
  """
  learning_rate = 0.2
  if epoch > 10:
    learning_rate = 0.02
  if epoch > 20:
    learning_rate = 0.01
  if epoch > 50:
    learning_rate = 0.005

  # tf.summary.scalar('learning rate', data=learning_rate, step=epoch)
  return learning_rate


callback = tf.keras.callbacks.LearningRateScheduler(scheduler)


model.compile(
    loss = keras.losses.CategoricalCrossentropy(),
    optimizer = keras.optimizers.SGD(learning_rate=0.1),
    metrics = ["accuracy",]
)


model.fit(X_train, y_train,batch_size=8, epochs=100, callbacks=[callback])


KERAS_MODEL_NAME = "checkpoints/ResNetLSTM4s.h5"
model.save(KERAS_MODEL_NAME)
convert_bytes(get_file_size(KERAS_MODEL_NAME), "MB")
test_loss, test_acc = model.evaluate(X_test,  y_test, verbose=2)
print('\nTest accuracy:', test_acc)


TF_LITE_MODEL_FILE_NAME = "checkpoints/ResNetLSTM4s.tflite"
tf_lite_converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = tf_lite_converter.convert()
tflite_model_name = TF_LITE_MODEL_FILE_NAME
open(tflite_model_name, "wb").write(tflite_model)
convert_bytes(get_file_size(TF_LITE_MODEL_FILE_NAME), "KB")
