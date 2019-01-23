import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split

n = 196591

checkins = pd.read_csv('Gowalla_totalCheckins.txt', sep = '\t', header = None)
checkins = checkins[[0, 4]]
checkins.columns = ['user', 'locationId']

clusters = pd.read_csv('answer.txt', sep = '\n', header = None, names = ['clusterId'])
clusters["user"] = pd.Series(range(n))

data = pd.merge(checkins, clusters,  how = 'inner', left_on = ['user'], right_on = ['user'])
users = np.unique(checkins['user'].values)

users_train, users_test = train_test_split(users)
data_test = data[np.in1d(data['user'], users_test)]

top10 = data.groupby('clusterId').apply(lambda x: x['locationId'].value_counts().nlargest(10).index.values)

countCorrect = 0
total = 0

for user in users_test:
    top_locations = top10[clusters[clusters['user'] == user]['clusterId']]
    users_locations = data_test[data_test['user'] == user]['locationId']
    
    correctAnswers = np.isin(top_locations, users_locations)
    
    total += 10
    countCorrect += np.sum(correctAnswers)

print(countCorrect / total * 100)