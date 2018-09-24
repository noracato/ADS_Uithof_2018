import csv
import numpy as np
import matplotlib.pyplot as plt
import math
# import matplotlib.mlab as mlab
# import scipy.stats as stats 

with open('_data/12a.csv', newline='') as data:
	reader = csv.reader(data, delimiter=';')

	# from 6 to 22 every 15 min = 16*4 = 64 timeslots
	timeslots_arr = [np.zeros(9) for x in range(64)]
	timeslots_lea = [np.zeros(9) for x in range(64)]
	timeslot_occ = np.zeros(64)

	next(reader)
	next(reader)

	for line in reader:

		time = line[2]
		if(time[1] == ":"):
			hour = int(time[0])
			minutes = int(time[2:4])
		else: 
			hour = int(time[0:2])
			minutes = int(time[3:5])

		timeslot = (hour - 6) * 4 + math.floor(minutes/15)

		
		timeslots_arr[timeslot] += [float(x) for x in line[3:12]]
		timeslots_lea[timeslot] += [float(x) for x in line[12:21]]
		timeslot_occ[timeslot] += 1

	print(timeslots_arr[1])

	for timeslot in range(64):
		occ = timeslot_occ[timeslot]
		timeslots_arr[timeslot] = [1/((x/occ)+0.000000000000000000000000001) for x in timeslots_arr[timeslot]]
		timeslots_lea[timeslot] = [1/((x/occ)+0.000000000000000000000000001) for x in timeslots_lea[timeslot]]

	# plt.bar(range(1,10), timeslots_arr[0])
	# plt.bar(range(1,10), timeslots_arr[1], color = '#d62728', bottom = timeslots_arr[0])
	# plt.bar(range(1,10), timeslots_arr[2], color = '#d00000', bottom = timeslots_arr[1]+timeslots_arr[0])
	# plt.bar(range(1,10), timeslots_arr[3], color = '#d00030',bottom = timeslots_arr[1]+timeslots_arr[0]+timeslots_arr[2])
	# plt.show()

# for stop in range(9):
# 	arriving = [x[stop] for x in timeslots_arr]
# 	print(arriving)
# 	leaving = [-1*x[stop] for x in timeslots_lea]
# 	plt.bar(range(1,65), arriving)
# 	plt.bar(range(1,65), leaving)
# 	plt.show()

with open('_data/lambdas12a.csv', 'w', newline='') as csvfile:
	writer = csv.writer(csvfile, delimiter=' ', quotechar='|', quoting=csv.QUOTE_MINIMAL)
	writer.writerow(range(9))
	for timeslot in range(64):
		writer.writerow([timeslot] + timeslots_arr[timeslot])
	for timeslot in range(64):
		writer.writerow([timeslot] + timeslots_arr[timeslot])

