import csv
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.mlab as mlab
import scipy.stats as stats 

def getline(stops):
	fit_alpha, fit_loc, fit_scale = stats.gamma.fit(stops)
	sort_stops = sorted(stops)
	length = len(sort_stops)
	y = np.empty(length)
	for i in range(length):
		number = (i - 0.5)/length
		new = stats.invgamma(fit_alpha, fit_loc, fit_scale)
		print(sort_stops[i], new.pdf(number))



# look at distribution fit for rutimes of bus
with open('runtimes.csv', newline='') as runtimes:
	reader = csv.reader(runtimes, delimiter=';')
	# we know 1259 entries
	# create entry for histogram of total travel time dist
	allsums = np.empty(1259)

	# create entry for histogram of travel time single stop
	singlestop2 = np.empty(1259)
	# singlestop3 = np.empty(1259)
	# singlestop4 = np.empty(1259)
	# singlestop5 = np.empty(1259)
	# singlestop6 = np.empty(1259)
	# singlestop7 = np.empty(1259)
	# singlestop8 = np.empty(1259)
	# singlestop9 = np.empty(1259)
	# singlestop10 = np.empty(1259)
	# singlestop11 = np.empty(1259)
	# singlestop12 = np.empty(1259)
	# singlestop13 = np.empty(1259)
	# singlestop14 = np.empty(1259)

	# start going through the rows
	for index, row in enumerate(reader):
		if index == 0:
			print(', '.join(row), len(row))
		else:
			introw = [int(x) for x in row]

			# sum to total travel time
			allsums[index-1] = sum(introw)

			# collect stations (14 stations total)
			# single station (1st stop)
			singlestop2[index-1] = introw[1]
			# singlestop3[index-1] = introw[2]
			# singlestop4[index-1] = introw[3]
			# singlestop5[index-1] = introw[4]
			# singlestop6[index-1] = introw[5]
			# singlestop7[index-1] = introw[6]
			# singlestop8[index-1] = introw[7]
			# singlestop9[index-1] = introw[8]
			# singlestop10[index-1] = introw[9]
			# singlestop11[index-1] = introw[10]
			# singlestop12[index-1] = introw[11]
			# singlestop13[index-1] = introw[12]
			# singlestop14[index-1] = introw[13]
		


	# plt.hist(allsums, bins='auto')
	# plt.hist(singlestop11, bins='auto')

	# fit normal
	# (mu, sigma) = norm.fit(singlestop11)
	# y = mlab.normpdf( bins, mu, sigma)

	# n, bins, patches = plt.hist(singlestop2, bins='auto', normed=True, facecolor='green', alpha=0.75)
	getline(singlestop2)

	# x = np.arange(0,250,.1) # stop 11
	# x = np.arange(950,1600,.1) # total stops
	# x = np.arange(10,250,.1)

	# fit_alpha, fit_loc, fit_scale = stats.gamma.fit(singlestop2)
	# print(fit_alpha, fit_scale, fit_loc)
	# y = stats.gamma.pdf(x, a=fit_alpha, loc=fit_loc, scale=fit_scale)


	
	# l = plt.plot(x, y, 'r--', linewidth=2)
	# plt.show()

	# print('stop 1,  var:', np.var(singlestop1), 'mean: ', np.mean(singlestop1))
	# print('stop 11,  var:', np.var(singlestop11), 'mean: ', np.mean(singlestop11))
	# print('total,  var:', np.var(allsums), 'mean: ', np.mean(allsums))




			
		