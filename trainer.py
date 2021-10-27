import pandas as pd
import numpy as np
import tsfresh
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import classification_report
import tensorflow as tf

import os

import matplotlib.pyplot as plt

# keras goodies
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Activation, Flatten, Conv1D, Dropout, MaxPooling1D, BatchNormalization
from tensorflow.keras.layers import LSTM, SimpleRNN, Bidirectional, GRU

from tensorflow.keras import optimizers
from tensorflow.keras import regularizers
from tensorflow.keras import metrics as kmetrics
import tensorflow.keras.backend as K

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

#clean_data_folder = "./Data/Clean"

""" for filename in os.listdir(clean_data_folder):
    full_path = f"{clean_data_folder}/{filename}"
    print(full_path)
    
    # load data into a DataFrame
    new_df = pd.read_csv(full_path)
    
    # merge into the base DataFrame
    base_df = pd.concat([base_df, new_df])


base_df.reset_index(drop=True, inplace=True) """



"""print(f"The data was collected using the sensors: {base_df.sensor_type.unique()}")
print(f"The data was collected for the activities: {base_df.activity_type.unique()}")
print(f"The number of unique recordings is: {len(base_df.recording_id.unique())}")
print(f"The subject IDs in the recordings are: {len(base_df.subject_id.unique())}")"""

# example_recording = base_df[base_df.activity_code == 1]
data_path = os.path.join(os.getcwd(), '2021', 'Respeck_recordings_clean.csv')
base_df = pd.read_csv(data_path, delimiter=',')

print(f"The data was collected using the sensors: {base_df.sensor_type.unique()}")
print(f"The data was collected for the activities: {base_df.activity_type.unique()}")
print(f"The number of unique recordings is: {len(base_df.recording_id.unique())}")
print(f"The subject IDs in the recordings are: {len(base_df.subject_id.unique())}")


#example_recording = base_df

"""window_size = 100 # 50 datapoints for the window size, which, at 25Hz, means 2 seconds
step_size = 50 # this is 50% overlap

window_number = 0 # start a counter at 0 to keep track of the window number

large_enough_windows = [window for window in example_recording.rolling(window=window_size, min_periods=window_size) if len(window) == window_size]

# we then get the windows with the required overlap

overlapping_windows = large_enough_windows[::step_size] 


# then we will append a window ID to each window
for window in overlapping_windows:
    window.loc[:, 'window_id'] = window_number
    window_number += 1"""

window_size = 50 # 50 datapoints for the window size, which, at 25Hz, means 2 seconds
step_size = 25 # this is 50% overlap

window_number = 0 # start a counter at 0 to keep track of the window number

all_overlapping_windows = []

for rid, group in base_df.groupby("recording_id"):
    print(f"Processing rid = {rid}")
    
    large_enough_windows = [window for window in group.rolling(window=window_size, min_periods=window_size) if len(window) == window_size]
    
    overlapping_windows = large_enough_windows[::step_size] 
    
    # then we will append a window ID to each window
    for window in overlapping_windows:
        window.loc[:, 'window_id'] = window_number
        window_number += 1
    
    if len(overlapping_windows) != 0:
        all_overlapping_windows.append(pd.concat(overlapping_windows).reset_index(drop=True))

# now we concatenate all the resulting windows
final_sliding_windows = pd.concat(all_overlapping_windows).reset_index(drop=True)

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
    #print(f"window_id = {window_id}")
    
    shape = group[columns_of_interest].values.shape
    #print(f"shape = {shape}")
    
    X.append(group[columns_of_interest].values)
    y.append(class_labels[group["activity_type"].values[0]])
    
X = np.asarray(X)
y = np.asarray(y)

"""print(f"X shape = {X.shape}")
print(f"y shape = {y.shape}")"""


X_train, X_test, y_train, y_test = train_test_split(X, y,
                                                    test_size=0.2, train_size=0.8)

y_train = np.asarray(pd.get_dummies(y_train), dtype=np.float32)
y_test = np.asarray(pd.get_dummies(y_test), dtype=np.float32)


"""print(f"X_train shape = {X_train.shape}")
print(f"y_train shape = {y_train.shape}")

print(f"X_test shape = {X_test.shape}")
print(f"y_test shape = {y_test.shape}")"""


filters = 64
kernel_size = 3
n_features = 6
activation='relu'
n_classes = 18


#Model 4 bidirectional LSTM model 

N_CLASSES = 18
N_HIDDEN_UNITS = 256

# define model
model = Sequential()

model.add(Bidirectional(LSTM(N_HIDDEN_UNITS, activation='tanh',return_sequences=True, kernel_initializer='he_normal'), input_shape=(window_size, n_features))) #, input_shape=(n_steps,1)))
model.add(Bidirectional(LSTM(N_HIDDEN_UNITS, activation='tanh',return_sequences=True, kernel_initializer='he_normal' ))) #, input_shape=(n_steps,1)))
model.add(Bidirectional(LSTM(N_HIDDEN_UNITS, activation='tanh',return_sequences=False, kernel_initializer='he_normal'))) #, input_shape=(n_steps,1)))

model.add(Dense(100, activation='tanh', kernel_initializer='he_normal'))
model.add(Dense(N_CLASSES, activation='softmax'))
print(model.summary())


# model.compile(
#     optimizer=optimizers.SGD(lr=0.01),
#     loss='categorical_crossentropy',
#     metrics = ['accuracy'])
model.compile(
    loss = keras.losses.CategoricalCrossentropy(),
    optimizer = keras.optimizers.Adam(lr = 0.001),
    metrics = ["accuracy"]
)


model.fit(X_train, y_train,batch_size=4, epochs=100)


""" KERAS_MODEL_NAME = "4s/biLSTM_model_new.h5"
model.save(KERAS_MODEL_NAME)
convert_bytes(get_file_size(KERAS_MODEL_NAME), "MB")
test_loss, test_acc = model.evaluate(X_test,  y_test, verbose=2)
print('\nTest accuracy:', test_acc)


TF_LITE_MODEL_FILE_NAME = "4s/biLSTM_tf_lite_model_new.tflite"
tf_lite_converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = tf_lite_converter.convert()
tflite_model_name = TF_LITE_MODEL_FILE_NAME
open(tflite_model_name, "wb").write(tflite_model)
convert_bytes(get_file_size(TF_LITE_MODEL_FILE_NAME), "KB")
 """