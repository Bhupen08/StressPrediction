import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

# Load CSV
df = pd.read_csv("radar_data.csv")

labels = df['Attribute'].tolist()
user_values = df['UserValue'].tolist()
avg_values = df['AverageValue'].tolist()

# Close the loop for values, NOT for labels
user_values += user_values[:1]
avg_values += avg_values[:1]

angles = np.linspace(0, 2 * np.pi, len(labels), endpoint=False).tolist()
angles += angles[:1]  # Only extend angles

# Create plot
fig, ax = plt.subplots(figsize=(8, 6), subplot_kw=dict(polar=True))

# Plot user
ax.plot(angles, user_values, label='User')
ax.fill(angles, user_values, alpha=0.25)

# Plot average
ax.plot(angles, avg_values, label='Average')
ax.fill(angles, avg_values, alpha=0.25)

# Set correct number of axes ticks (no duplicate label)
ax.set_thetagrids(np.degrees(angles[:-1]), labels)

ax.set_title("User vs Average Radar Chart")
ax.legend(loc='upper right')
plt.tight_layout()
plt.show()
