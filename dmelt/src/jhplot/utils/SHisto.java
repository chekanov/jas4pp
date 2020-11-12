package jhplot.utils;


/**
 * This class represents a histogram operation class. Some parts of this class
 * are not free for commercial use and licensed by JAVA RESEARCH LICENSE. Please
 * look at https://jai.dev.java.net/jrl.html.
 * The program is based on JAI Histogram.java class.
 * 
 * @author S.Chekanov and others
 * 
 */
public class SHisto {

	/** The number of bins*/
	private int[] numBins;

	/**
	 * The lowest i value.
	 */
	private double[] lowValue;

	/**
	 * The highest value.
	 */
	private double[] highValue;

	/**
	 * The number of bands of the image from which the histogram is accumulated.
	 */
	private int numBands;

	/** The width of a bin for each band. */
	private double[] binWidth;

	/** The bins for each band, used to hold the number counts. */
	private double[][] bins = null;

	/** The total bin count over all bins for each band. */
	private int[] totals = null;

	/** The mean value over all bins for each band. */
	private double[] mean = null;

	/**
	 * Copy an int array into a new int array of a given length padding with
	 * zeroth element if needed.
	 * 
	 */
	private static final int[] fill(int[] array, int newLength) {
		int[] newArray = null;

		if (array == null || array.length == 0) {
			System.out.println("NULL ARRAY!");
			return newArray;
		} else if (newLength > 0) {
			newArray = new int[newLength];
			int oldLength = array.length;
			for (int i = 0; i < newLength; i++) {
				if (i < oldLength) {
					newArray[i] = array[i];
				} else {
					newArray[i] = array[0];
				}
			}
		}
		return newArray;
	}

	/**
	 * Copy an double array into a new double array of a given length padding
	 * with zeroth element if needed.
	 */
	private static final double[] fill(double[] array, int newLength) {
		double[] newArray = null;

		if (array == null || array.length == 0) {
			System.out.println("NULL ARRAY!");
			return newArray;

		} else if (newLength > 0) {
			newArray = new double[newLength];
			int oldLength = array.length;
			for (int i = 0; i < newLength; i++) {
				if (i < oldLength) {
					newArray[i] = array[i];
				} else {
					newArray[i] = array[0];
				}
			}
		}
		return newArray;
	}

	/**
	 * Constructor.
	 * 
	 * <p>
	 * This constructor should be used when <code>numBins</code>,
	 * <code>lowValue</code>, and/or <code>highValues</code> are different
	 * for each band of the image. The length of the arrays indicates the number
	 * of bands the image has, and the three arrays must have the same length.
	 * 
	 * <p>
	 * Since this constructor has no way of knowing the actual number of bands
	 * of the image, the length of the arrays is not checked against anything.
	 * Therefore, it is very important that the caller supplies the correct
	 * arrays of correct lengths, or errors will occur.
	 * 
	 * @param numBins
	 *            The number of bins for each band of the image. The length of
	 *            this array indicates the number of bands for this histogram.
	 * @param lowValue
	 *            The lowest inclusive pixel value checked for each band.
	 * @param highValue
	 *            The highest exclusive pixel value checked for each band.
	 * 
	 */
	public SHisto(int[] numBins, double[] lowValue, double[] highValue) {

		if (numBins == null || lowValue == null || highValue == null) {

			System.out.println("WRONG DEFINITIONS!");
			return;
		}

		numBands = numBins.length;

		if (lowValue.length != numBands || highValue.length != numBands) {
			System.out.println("WRONG DEFINITIONS-0!");
			return;
		}

		if (numBands == 0) {
			System.out.println("WRONG DEFINITIONS-1!");
		}

		for (int i = 0; i < numBands; i++) {
			if (numBins[i] <= 0) {
				System.out.println("WRONG DEFINITIONS-2!");
				return;
			}

			if (lowValue[i] >= highValue[i]) {
				System.out.println("WRONG DEFINITIONS-3!");
				return;
			}
		}

		this.numBins = (int[]) numBins.clone();
		this.lowValue = (double[]) lowValue.clone();
		this.highValue = (double[]) highValue.clone();

		binWidth = new double[numBands];

		// Compute binWidth for all bands.
		for (int i = 0; i < numBands; i++) {
			binWidth[i] = (highValue[i] - lowValue[i]) / numBins[i];
		}
	}

	/**
	 * Constructor.
	 * 
	 * <p>
	 * This constructor should be used when <code>numBins</code>,
	 * <code>lowValue</code>, and/or <code>highValues</code> may be
	 * different for each band of the image. The number of bands in the image is
	 * provided explicitly. If any of the arrays provided has a length which is
	 * less than the number of bands, the first element in that array is used to
	 * fill out the array to a length of <code>numBands</code>.
	 * 
	 * 
	 * @param numBins
	 *            The number of bins for each band of the image.
	 * @param lowValue
	 *            The lowest value checked for each band.
	 * @param highValue
	 *            The highest value checked for each band.
	 * @param numBands
	 *            The number of bands in the image.
	 * 
	 * 
	 */
	public SHisto(int[] numBins, double[] lowValue, double[] highValue,
			int numBands) {
		this(fill(numBins, numBands), fill(lowValue, numBands), fill(highValue,
				numBands));
	}

	/**
	 * Constructor.
	 * 
	 * <p>
	 * The same <code>numBins</code>, <code>lowValue</code>, and
	 * <code>highValue</code> is applied to every band of the image.
	 * 
	 * @param numBins
	 *            The number of bins for all bands of the image.
	 * @param lowValue
	 *            The lowest inclusive pixel value checked for all bands.
	 * @param highValue
	 *            The highest exclusive pixel value checked for all bands.
	 * @param numBands
	 *            The number of bands of the image.
	 * 
	 */
	public SHisto(int numBins, double lowValue, double highValue,
			int numBands) {
		if (numBands <= 0) {
			System.out.println("WRONG DEFINITION");
			return;
		}

		if (numBins <= 0) {
			System.out.println("WRONG DEFINITION");
			return;
		}

		if (lowValue >= highValue) {
			System.out.println("WRONG DEFINITION");
			return;
		}

		this.numBands = numBands;
		this.numBins = new int[numBands];
		this.lowValue = new double[numBands];
		this.highValue = new double[numBands];
		this.binWidth = new double[numBands];

		double bw = (highValue - lowValue) / numBins; // binWidth

		bins = new double[numBands][numBins];
		
		for (int i = 0; i < numBands; i++) {
			this.numBins[i] = numBins;
			this.lowValue[i] = lowValue;
			this.highValue[i] = highValue;
			this.binWidth[i] = bw;
		}
	}

	/** Returns the number of bins of the histogram for all bands. */
	public int[] getNumBins() {
		return (int[]) numBins.clone();
	}

	/**
	 * Returns the number of bins of the histogram for a specific band.
	 * 
	 * @param band
	 *            The index of the band whose <code>numBins</code> is to be
	 *            returned.
	 */
	public int getNumBins(int band) {
		return numBins[band];
	}

	/** Returns the lowest inclusive pixel value checked for all bands. */
	public double[] getLowValue() {
		return (double[]) lowValue.clone();
	}

	/**
	 * Returns the lowest inclusive pixel value checked for a specific band.
	 * 
	 * @param band
	 *            The index of the band whose <code>lowValue</code> is to be
	 *            returned.

	 */
	public double getLowValue(int band) {
		return lowValue[band];
	}

	/** Returns the highest exclusive pixel value checked for all bands. */
	public double[] getHighValue() {
		return (double[]) highValue.clone();
	}

	/**
	 * Returns the highest  value checked for a specific band.
	 * 
	 * @param band
	 *            The index of the band whose <code>highValue</code> is to be
	 *            returned.
	 */
	public double getHighValue(int band) {
		return highValue[band];
	}

	/**
	 * Returns the number of bands of the histogram. This value is the same as
	 * the number of bands of the bins, <code>bins.length</code>.
	 */
	public int getNumBands() {
		return numBands;
	}

	/**
	 * Returns the array of bands of bins, each bin is the histogram for a band,
	 * i.e., the format of the returned array is <code>int[bands][bins]</code>.
	 */
	public double[][] getBins() {
		if (bins == null) {
			bins = new double[numBands][];

			for (int i = 0; i < numBands; i++) {
				bins[i] = new double[numBins[i]];
			}
		}

		return bins;
	}

	
	
	/**
	 * Set bin values
	 * @param xbin bin values for a specific band
	 */
	public void setBins(double[][] xbin) {
		if (bins == null) {
			bins = new double[numBands][];

			for (int i = 0; i < numBands; i++) {
				for (int j = 0; j < numBins[i]; j++) {
				bins[i][j] = xbin[i][j];
			}
		};
		}
	}
	
	/**
	 * Set bin values assuming that there only one band
	 * @param xbin bin values for a specific band
	 */
	public void setBins(double[] xbin) {
		
				for (int j = 0; j < numBins[0]; j++) {
				bins[0][j] = xbin[j];		
		      //  System.out.println("INPUT0="+bins[0][j]);
	}
	}
	
	
	
	/**
	 * Returns the bins of the histogram for a specific band by reference.
	 * 
	 * @param band
	 *            The index of the band whose <code>bins</code> are to be
	 *            returned.

	 */
	public double[] getBins(int band) {
		getBins(); // make sure bins is initialized
		return bins[band];
	}

	

	/**
	 * Returns the bins of the first band.
	 * @param bins from the first band
	 */
	public double[] getBinsFirstBand() {
		return bins[0];
	}
	
	/**
	 * Returns the bin content  of the first band.
	 * @param bin index
	 */
	public double getBinsFirstBand(int j) {
		return bins[0][j];
	}
	
	
	
	/**
	 * Returns the number of pixel samples found in a given bin for a specific
	 * band.
	 * 
	 * @param band
	 *            The index of the band-of-interest.
	 * @param bin
	 *            The index of the bin whose value is to be returned.
	 */
	public double getBinSize(int band, int bin) {
		getBins(); // make sure bins is initialized
		return bins[band][bin];
	}

	/**
	 * Returns the lowest inclusive pixel value of a given bin for a specific
	 * band.
	 * 
	 * @param band
	 *            The index of the band-of-interest.
	 * @param bin
	 *            The index of the bin whose <code>lowValue</code> is to be
	 *            returned.
	 */
	public double getBinLowValue(int band, int bin) {
		return lowValue[band] + bin * binWidth[band];
	}

	/**
	 * Resets the values of all bins to zero. If <code>bins</code> has not
	 * been initialized (<code>null</code>), this method does nothing.
	 */
	public void clearHistogram() {
		if (bins != null) {
			synchronized (bins) {
				for (int i = 0; i < numBands; i++) {
					double[] b = bins[i];
					int length = b.length;

					for (int j = 0; j < length; j++) {
						b[j] = 0;
					}
				}
			}
		}
	}

	/**
	 * Returns the total bin count over all bins for all bands.
	 * 
	 * <p>
	 * An array which stores the total bin count is kept in this class and a
	 * reference to this array is returned by this method for performance
	 * reasons. The elements of the returned array should not be modified or
	 * undefined errors may occur. The array format is
	 * <code>int[numBands]</code>.
	 * 
	 */
	public int[] getTotals() {
		if (totals == null) {
			getBins(); // make sure bins is initialized

			synchronized (this) {
				totals = new int[numBands];

				for (int i = 0; i < numBands; i++) {
					double[] b = bins[i];
					int length = b.length;
					int t = 0;

					for (int j = 0; j < length; j++) {
						t += b[j];
					}

					totals[i] = t;
				}
			}
		}

		return totals;
	}

	/**
	 * Returns the total bin count for the specified sub-range (via "min" and
	 * "max" bin) of the indicated band. The sub-rangee must fall within the
	 * actual range of the bins.
	 * 
	 * @param band
	 *            The index of the band-of-interest.
	 * @param minBin
	 *            The minimum bin index to be counted.
	 * @param maxBin
	 *            The maximum bin index to be counted.
	 * 
	 * 
	 */
	public int getSubTotal(int band, int minBin, int maxBin) {
		if (minBin < 0 || maxBin >= numBins[band]) {
			return -1;
		}

		if (minBin > maxBin) {
			return -1;
		}

		double[] b = getBins(band);
		int total = 0;

		for (int i = minBin; i <= maxBin; i++) {
			total += b[i];
		}

		return total;
	}

	/**
	 * Returns the mean values for all bands of the histogram.
	 * 
	 */
	public double[] getMean() {
		if (mean == null) {
			getTotals(); // make sure totals is computed

			synchronized (this) {
				mean = new double[numBands];

				for (int i = 0; i < numBands; i++) {
					double[] counts = getBins(i);
					int nBins = numBins[i];
					double level = getLowValue(i);
					double bw = binWidth[i];

					double mu = 0.0;
					double total = totals[i];

					for (int b = 0; b < nBins; b++) {
						mu += (counts[b] / total) * level;
						level += bw;
					}

					mean[i] = mu;
				}
			}
		}

		return mean;
	}

	

	/**
	 * Returns a specified (absolute) (central) moment of the histogram.
	 * 
	 * <p>
	 * The <i>i</i>th moment in each band is defined to be the mean of the
	 * image pixel values raised to the <i>i</i>th power in that band. For
	 * central moments the average of the <i>i</i>th power of the deviation
	 * from the mean is used. For absolute moments the absolute value of the
	 * exponentiated term is used.
	 * 
	 * <p>
	 * Note that the mean is the first moment, the average energy the second
	 * central moment, etc.
	 * 
	 * @param moment
	 *            The moment number or index which must be positive or an
	 *            <code>IllegalArgumentException</code> will be thrown.
	 * @param isAbsolute
	 *            Whether to calculate the absolute moment.
	 * @param isCentral
	 *            Whether to calculate the central moment.
	 * @return The requested (absolute) (central) moment of the histogram.
	 * 
	 */
	public double[] getMoment(int moment, boolean isAbsolute, boolean isCentral) {
		// Check for non-positive moment number.
		if (moment < 1) {
			System.out.println("Moment is smaller than 1");
			return null;
		}

		// If the mean is required but has not yet been calculated
		// then calculate it first.
		if ((moment == 1 || isCentral) && mean == null) {
			getMean();
		}

		// If it's the first non-absolute, non-central moment return the mean.
		if ((moment == 1) && !isAbsolute && !isCentral) {
			return mean;
		}

		double[] moments = new double[numBands];

		// For the trivial case of first central moment return zeros.
		if (moment == 1 && isCentral) {
			for (int band = 0; band < numBands; band++) {
				moments[band] = 0.0;
			}
		} else {
			// Get the total counts for all bands.
			getTotals();

			for (int band = 0; band < numBands; band++) {
				// Cache some band-dependent quantities.
				double[] counts = getBins(band);
				int nBins = numBins[band];
				double level = getLowValue(band);
				double bw = binWidth[band];
				double total = totals[band];

				// Clear the moment value for this band.
				double mmt = 0.0;

				if (isCentral) {
					// Cache the mean value for this band.
					double mu = mean[band];

					if (isAbsolute && moment % 2 == 0) {
						// Even moment so absolute value has no effect.
						for (int b = 0; b < nBins; b++) {
							mmt += Math.pow(level - mu, moment) * counts[b]
									/ total;
							level += bw;
						}
					} else {
						// Odd moment so need to use absolute value.
						for (int b = 0; b < nBins; b++) {
							mmt += Math.abs(Math.pow(level - mu, moment))
									* counts[b] / total;
							level += bw;
						}
					}
				} else if (isAbsolute && moment % 2 != 0) {
					// Odd moment so need to use absolute value.
					for (int b = 0; b < nBins; b++) {
						mmt += Math.abs(Math.pow(level, moment)) * counts[b]
								/ total;
						level += bw;
					}
				} else {
					// Even or non-absolute non-central moment.
					for (int b = 0; b < nBins; b++) {
						mmt += Math.pow(level, moment) * counts[b] / total;
						level += bw;
					}
				}

				// Save the result.
				moments[band] = mmt;
			}
		}

		return moments;
	}

	/**
	 * Returns the standard deviation for all bands of the histogram. This is a
	 * convenience method as the returned values could be calculated using the
	 * first and second moments which are available via the moment generation
	 * function.
	 * 
	 * @return The standard deviation values for all bands.
	 *
	 */
	public double[] getStandardDeviation() {
		getMean();

		double[] variance = getMoment(2, false, false);

		double[] stdev = new double[numBands];

		for (int i = 0; i < variance.length; i++) {
			stdev[i] = Math.sqrt(variance[i] - mean[i] * mean[i]);
		}

		return stdev;
	}

	/**
	 * Returns the entropy of the histogram.
	 * 
	 * <p>
	 * The histogram entropy is defined to be the negation of the sum of the
	 * products of the probability associated with each bin with the base-2 log
	 * of the probability.
	 * 
	 * @return The entropy of the histogram.
	 * 
	 */
	public double[] getEntropy() {
		// Get the total counts for all bands.
		getTotals();

		double log2 = Math.log(2.0);

		double[] entropy = new double[numBands];

		for (int band = 0; band < numBands; band++) {
			double[] counts = getBins(band);
			int nBins = numBins[band];
			double total = totals[band];

			double H = 0.0;

			for (int b = 0; b < nBins; b++) {
				double p = counts[b] / total;
				if (p != 0.0) {
					H -= p * (Math.log(p) / log2);
				}
			}

			entropy[band] = H;
		}

		return entropy;
	}

	/**
	 * Computes a smoothed version of the histogram.
	 * 
	 * <p>
	 * Each band of the histogram is smoothed by averaging over a moving window
	 * of a size specified by the method parameter: if the value of the
	 * parameter is <i>k</i> then the width of the window is <i>2*k + 1</i>.
	 * If the window runs off the end of the histogram only those values which
	 * intersect the histogram are taken into consideration. The smoothing may
	 * optionally be weighted to favor the central value using a "triangular"
	 * weighting. For example, for a value of <i>k</i> equal to 2 the central
	 * bin would have weight 1/3, the adjacent bins 2/9, and the next adjacent
	 * bins 1/9.
	 * 
	 * @param isWeighted
	 *            Whether bins will be weighted using a triangular weighting
	 *            scheme favoring bins near the central bin.
	 * @param k
	 *            The smoothing parameter which must be non-negative or an
	 *            <code>IllegalArgumentException</code> will be thrown. If
	 *            zero, the histogram object will be returned with no smoothing
	 *            applied.
	 * @return A smoothed version of the histogram.
	 * 
	 */
	public SHisto getSmoothed(boolean isWeighted, int k) {
		if (k < 0) {
			System.out.println("Smoothing is smaller than 0!");
			return null;
		} else if (k == 0) {
			return this;
		}

		// Create a new, identical but empty Histogram.
		SHisto smoothedHistogram = new SHisto(getNumBins(),
				getLowValue(), getHighValue());

		// Get a reference to the bins of the new Histogram.
		double[][] smoothedBins = smoothedHistogram.getBins();

		/*
		 for (int i=0; i<numBins[0]; i++){
			 System.out.println( "INPUT="+bins[0][i]);
		 }
		 */
		
		
		// Get the total counts for all bands.
		getTotals();

		// Initialize the smoothing weights if needed.
		double[] weights = null;
		if (isWeighted) {
			int numWeights = 2 * k + 1;
			double denom = numWeights * numWeights;
			weights = new double[numWeights];
			for (int i = 0; i <= k; i++) {
				weights[i] = (i + 1) / denom;
			}
			for (int i = k + 1; i < numWeights; i++) {
				weights[i] = weights[numWeights - 1 - i];
			}
		}

		for (int band = 0; band < numBands; band++) {
			// Cache bin-dependent values and references.
			double[] counts = getBins(band);
			double[] smoothedCounts = smoothedBins[band];
			int nBins = smoothedHistogram.getNumBins(band);

			// Clear the band total count for the smoothed histogram.
			double sum = 0;

			if (isWeighted) {
				for (int b = 0; b < nBins; b++) {
					// Determine clipped range.
					int min = Math.max(b - k, 0);
					int max = Math.min(b + k, nBins);

					// Calculate the offset into the weight array.
					int offset = k > b ? k - b : 0;

					// Accumulate the total for the range.
					double acc = 0;
					double weightTotal = 0;
					for (int i = min; i < max; i++) {
						double w = weights[offset++];
						acc += counts[i] * w;
						weightTotal += w;
					}

					// Round the accumulated value.
					smoothedCounts[b] =  (acc / weightTotal + 0.5);

					// Accumulate total for band.
					sum += smoothedCounts[b];
				}
			} else {
				for (int b = 0; b < nBins; b++) {
					// Determine clipped range.
					int min = Math.max(b - k, 0);
					int max = Math.min(b + k, nBins);

					// Accumulate the total for the range.
					int acc = 0;
					for (int i = min; i < max; i++) {
						acc += counts[i];
					}

					// Calculate the average for the range.
					smoothedCounts[b] = (acc / (double) (max - min + 1) + 0.5);

					// Accumulate total for band.
					sum += smoothedCounts[b];
				}
			}

			// Rescale the counts such that the band total is approximately
			// the same as for the same band of the original histogram.
			double factor = (double) totals[band] / (double) sum;
			for (int b = 0; b < nBins; b++) {
			
			
				smoothedCounts[b] = (smoothedCounts[b] * factor + 0.5);
			//	System.out.println( smoothedCounts[b]);
			}
	
			//smoothedHistogram.setBins( smoothedCounts);
		}

		
		
		return smoothedHistogram;
	}

	/**
	 * Computes a Gaussian smoothed version of the histogram.
	 * 
	 * <p>
	 * Each band of the histogram is smoothed by discrete convolution with a
	 * kernel approximating a Gaussian impulse response with the specified
	 * standard deviation.
	 * 
	 * @param standardDeviation
	 *            The standard deviation of the Gaussian smoothing kernel which
	 *            must be non-negative or an
	 *            <code>IllegalArgumentException</code> will be thrown. If
	 *            zero, the histogram object will be returned with no smoothing
	 *            applied.
	 * @return A Gaussian smoothed version of the histogram.
	 * 
	 */
	public SHisto getGaussianSmoothed(double standardDeviation) {
		if (standardDeviation < 0.0) {
			System.out.println("Standard deviation cannot be smaller than 0");
			return null;

		} else if (standardDeviation == 0.0) {
			return this;
		}

		// Create a new, identical but empty Histogram.
		SHisto smoothedHistogram = new SHisto(getNumBins(),
				getLowValue(), getHighValue());

		// Get a reference to the bins of the new Histogram.
		double[][] smoothedBins = smoothedHistogram.getBins();

		// Get the total counts for all bands.
		getTotals();

		// Determine the number of weights (must be odd).
		int numWeights = (int) (2 * 2.58 * standardDeviation + 0.5);
		if (numWeights % 2 == 0) {
			numWeights++;
		}

		// Initialize the smoothing weights.
		double[] weights = new double[numWeights];
		int m = numWeights / 2;
		double var = standardDeviation * standardDeviation;
		double gain = 1.0 / Math.sqrt(2.0 * Math.PI * var);
		double exp = -1.0 / (2.0 * var);
		for (int i = m; i < numWeights; i++) {
			double del = i - m;
			weights[i] = weights[numWeights - 1 - i] = gain
					* Math.exp(exp * del * del);
		}

		for (int band = 0; band < numBands; band++) {
			// Cache bin-dependent values and references.
			double[] counts = getBins(band);
			double[] smoothedCounts = smoothedBins[band];
			int nBins = smoothedHistogram.getNumBins(band);

			// Clear the band total count for the smoothed histogram.
			int sum = 0;

			for (int b = 0; b < nBins; b++) {
				// Determine clipped range.
				int min = Math.max(b - m, 0);
				int max = Math.min(b + m, nBins);

				// Calculate the offset into the weight array.
				int offset = m > b ? m - b : 0;

				// Accumulate the total for the range.
				double acc = 0;
				double weightTotal = 0;
				for (int i = min; i < max; i++) {
					double w = weights[offset++];
					acc += counts[i] * w;
					weightTotal += w;
				}

				// Round the accumulated value.
				smoothedCounts[b] = (int) (acc / weightTotal + 0.5);

				// Accumulate total for band.
				sum += smoothedCounts[b];
			}

			// Rescale the counts such that the band total is approximately
			// the same as for the same band of the original histogram.
			double factor = (double) totals[band] / (double) sum;
			for (int b = 0; b < nBins; b++) {
				smoothedCounts[b] = (int) (smoothedCounts[b] * factor + 0.5);
			}
		}

		return smoothedHistogram;
	}

	/**
	 * Calculates the <i>p-tile</i> threshold.
	 * 
	 * <p>
	 * Computes thresholds such that a specified proportion of the sample values
	 * in each band are below the threshold.
	 * 
	 * @param p
	 *            The proportion of samples in each band which should be below
	 *            the threshold in the band. If <code>p</code> is not in the
	 *            range (0.0,&nbsp;1.0) an <code>IllegalArgumentException</code>
	 *            will be thrown.
	 * @return The requested <i>p-tile</i> thresholds.
	 * 
	 */
	public double[] getPTileThreshold(double p) {
		if (p <= 0.0 || p >= 1.0) {
			System.out.println("p <= or p>0.  Wrong definition!");
		}

		double[] thresholds = new double[numBands];
		getTotals();

		for (int band = 0; band < numBands; band++) {
			// Cache some band-dependent values.
			//int nBins = numBins[band];
			double[] counts = getBins(band);

			// Calculate the total count for this band.
			int totalCount = totals[band];

			// Determine the number of binWidths to add to the lowValue
			// to get the desired threshold.
			int numBinWidths = 0;
			double count = counts[0];
			int idx = 0;
			while ((double) count / (double) totalCount < p) {
				numBinWidths++;
				count += counts[++idx];
			}

			// Calculate the threshold.
			thresholds[band] = getLowValue(band) + numBinWidths
					* binWidth[band];
		}

		return thresholds;
	}

	/**
	 * Calculates the threshold using the mode method.
	 * 
	 * <p>
	 * The threshold is defined to be the minimum between two peaks. The first
	 * peak is the highest peak in the histogram. The second peak is the highest
	 * peak in the histogram weighted by a specified power of the distance from
	 * the first peak.
	 * 
	 * @param power
	 *            The exponent of the distance weighting from the first peak.
	 * @return The requested thresholds.
	 * 
	 */
	public double[] getModeThreshold(double power) {
		double[] thresholds = new double[numBands];
		getTotals();

		for (int band = 0; band < numBands; band++) {
			// Cache some band-dependent values.
			int nBins = numBins[band];
			double[] counts = getBins(band);

			// Find the primary mode (highest peak).
			int mode1 = 0;
			double mode1Count = counts[0];
			for (int b = 1; b < nBins; b++) {
				if (counts[b] > mode1Count) {
					mode1 = b;
					mode1Count = counts[b];
				}
			}

			// Find the secondary mode (highest weighted peak).
			int mode2 = -1;
			double mode2count = 0.0;
			for (int b = 0; b < nBins; b++) {
				double d = counts[b] * Math.pow(Math.abs(b - mode1), power);
				if (d > mode2count) {
					mode2 = b;
					mode2count = d;
				}
			}

			// Find the minimum value between the two peaks.
			int min = mode1;
			double minCount = counts[mode1];
			for (int b = mode1 + 1; b <= mode2; b++) {
				if (counts[b] < minCount) {
					min = b;
					minCount = counts[b];
				}
			}

			thresholds[band] = (int) ((mode1 + mode2) / 2.0 + 0.5);
		}

		return thresholds;
	}

	/**
	 * Calculates the threshold using iterative bisection.
	 * 
	 * For each band an initial threshold is defined to be the midpoint of the
	 * range of data represented by the histogram. The mean value is calculated
	 * for each sub-histogram and a new threshold is defined as the arithmetic
	 * mean of the two sub-histogram means. This process is repeated until the
	 * threshold value no longer changes.
	 * 
	 * @return The requested thresholds.
	 * 
	 */
	public double[] getIterativeThreshold() {
		double[] thresholds = new double[numBands];
		getTotals();

		for (int band = 0; band < numBands; band++) {
			// Cache some band-dependent values.
			int nBins = numBins[band];
			double[] counts = getBins(band);
			double bw = binWidth[band];

			// Set intial threshold to midpoint of data range for this band.
			double threshold = 0.5 * (getLowValue(band) + getHighValue(band));
			double mid1 = 0.5 * (getLowValue(band) + threshold);
			double mid2 = 0.5 * (threshold + getHighValue(band));

			// Iterate only if total is nonzero.
			if (totals[band] != 0) {

				// Loop until threshold estimate no longer changes.
				int countDown = 1000;
				do {
					// Save band threshold for this iteration.
					thresholds[band] = threshold;

					// Cache the total count for this band.
					double total = totals[band];

					// Initialize the level corresponding to a bin.
					double level = getLowValue(band);

					// Clear mean values for sub-ranges.
					double mean1 = 0.0;
					double mean2 = 0.0;

					// Clear sub-range 1 count.
					int count1 = 0;

					// Calculate the mean values for the two sub-ranges.
					for (int b = 0; b < nBins; b++) {
						// Update the mean value for the appropriate sub-range.
						if (level <= threshold) {
							double c = counts[b];
							mean1 += c * level;
							count1 += c;
						} else {
							mean2 += counts[b] * level;
						}

						// Augment the level for the current bin by the bin
						// width.
						level += bw;
					}

					// Rescale values using sub-range totals.
					if (count1 != 0) {
						mean1 /= count1;
					} else {
						mean1 = mid1;
					}
					if (total != count1) {
						mean2 /= (total - count1);
					} else {
						mean2 = mid2;
					}

					// Update the threshold estimate.
					threshold = 0.5 * (mean1 + mean2);
				} while (Math.abs(threshold - thresholds[band]) > 1e-6
						&& --countDown > 0);
			} else {
				thresholds[band] = threshold;
			}
		}

		return thresholds;
	}

	/**
	 * Calculates the threshold which maximizes the ratio of the between-class
	 * variance to the within-class variance for each band.
	 * 
	 * @return The requested thresholds.
	 * 
	 */
	public double[] getMaxVarianceThreshold() {
		double[] thresholds = new double[numBands];
		getTotals();
		getMean();
		//double[] variance = getMoment(2, false, false);

		for (int band = 0; band < numBands; band++) {
			// Cache some band-dependent values.
			int nBins = numBins[band];
			double[] counts = getBins(band);
			double total = totals[band];
			double mBand = mean[band];
			double bw = binWidth[band];

			double prob0 = 0.0;
			double mean0 = 0.0;
			double lv = getLowValue(band);
			double level = lv;
			double maxRatio = -Double.MAX_VALUE;
			int maxIndex = 0;
			int runLength = 0;

			for (int t = 0; t < nBins; t++, level += bw) {
				double p = counts[t] / total;
				prob0 += p;
				if (prob0 == 0.0) {
					continue;
				}

				double m0 = (mean0 += p * level) / prob0;

				double prob1 = 1.0 - prob0;

				if (prob1 == 0.0) {
					continue;
				}

				double m1 = (mBand - mean0) / prob1;

				double var0 = 0.0;
				double g = lv;
				for (int b = 0; b <= t; b++, g += bw) {
					double del = g - m0;
					var0 += del * del * counts[b];
				}
				var0 /= total;

				double var1 = 0.0;
				for (int b = t + 1; b < nBins; b++, g += bw) {
					double del = g - m1;
					var1 += del * del * counts[b];
				}
				var1 /= total;

				if (var0 == 0.0 && var1 == 0.0 && m1 != 0.0) {
					maxIndex = (int) (((m0 + m1) / 2.0 - getLowValue(band))
							/ bw + 0.5);
					runLength = 0;
					break;
				}

				if (var0 / prob0 < 0.5 || var1 / prob1 < 0.5) {
					continue;
				}

				double mdel = m0 - m1;
				double ratio = prob0 * prob1 * mdel * mdel / (var0 + var1);

				if (ratio > maxRatio) {
					maxRatio = ratio;
					maxIndex = t;
					runLength = 0;
				} else if (ratio == maxRatio) {
					runLength++;
				}
			}

			thresholds[band] = getLowValue(band)
					+ (maxIndex + runLength / 2.0 + 0.5) * bw;
		}

		return thresholds;
	}

	/**
	 * Calculates the threshold which maximizes the entropy.
	 * 
	 * <p>
	 * The <i>entropy</i> of a range of gray levels is defined to be the
	 * negation of the sum of products of the probability and the logarithm
	 * thereof over all gray levels in the range. The maximum entropy threshold
	 * is defined to be that value which maximizes the sum of the entropy of the
	 * two ranges which are above and below the threshold, respectively. This
	 * computation is effected for each band.
	 * 
	 * @return The requested thresholds.
	 * 
	 */
	public double[] getMaxEntropyThreshold() {
		double[] thresholds = new double[numBands];
		getTotals();

		double[] entropy = getEntropy();

		double log2 = Math.log(2.0);

		for (int band = 0; band < numBands; band++) {
			// Cache some band-dependent values.
			int nBins = numBins[band];
			double[] counts = getBins(band);
			double total = totals[band];
			double H = entropy[band];

			double P1 = 0.0;
			double H1 = 0.0;

			double maxCriterion = -Double.MAX_VALUE;
			int maxIndex = 0;
			int runLength = 0;

			for (int t = 0; t < nBins; t++) {
				double p = counts[t] / total;

				if (p == 0.0) {
					continue;
				}

				P1 += p;

				H1 -= p * Math.log(p) / log2;

				double max1 = 0.0;
				for (int b = 0; b <= t; b++) {
					if (counts[b] > max1) {
						max1 = counts[b];
					}
				}

				if (max1 == 0.0) {
					continue;
				}

				double max2 = 0.0;
				for (int b = t + 1; b < nBins; b++) {
					if (counts[b] > max2) {
						max2 = counts[b];
					}
				}

				if (max2 == 0.0) {
					continue;
				}

				double ratio = H1 / H;

				double criterion = ratio * Math.log(P1)
						/ Math.log(max1 / total) + (1.0 - ratio)
						* Math.log(1.0 - P1) / Math.log(max2 / total);

				if (criterion > maxCriterion) {
					maxCriterion = criterion;
					maxIndex = t;
					runLength = 0;
				} else if (criterion == maxCriterion) {
					runLength++;
				}
			}

			thresholds[band] = getLowValue(band)
					+ (maxIndex + runLength / 2.0 + 0.5) * binWidth[band];
		}

		return thresholds;
	}

	/**
	 * Calculates the threshold which minimizes the probability of error.
	 * 
	 * <p>
	 * For each band the histogram is modeled as the sum of two Gaussian
	 * distributions and the threshold which minimizes the misclassification
	 * error is computed. If the underlying histogram is unimodal the mean value
	 * of each band will be returned as the threshold. The bimodality of the
	 * histogram for that band will be identically zero.
	 * 
	 * @return The requested thresholds.
	 * 
	 */
	public double[] getMinErrorThreshold() {
		double[] thresholds = new double[numBands];
		getTotals();
		getMean();

		for (int band = 0; band < numBands; band++) {
			// Cache some band-dependent values.
			int nBins = numBins[band];
			double[] counts = getBins(band);
			double total = totals[band];
			double lv = getLowValue(band);
			double bw = binWidth[band];

			double total1 = 0;
			double total2 = totals[band];
			double sum1 = 0.0;
			double sum2 = mean[band] * total;

			double level = lv;

			double minCriterion = Double.MAX_VALUE;
			int minIndex = 0;
			int runLength = 0;

			double J0 = Double.MAX_VALUE;
			double J1 = Double.MAX_VALUE;
			double J2 = Double.MAX_VALUE;
			int Jcount = 0;

			for (int t = 0; t < nBins; t++, level += bw) {
				double c = counts[t];

				total1 += c;
				total2 -= c;

				double incr = level * c;
				sum1 += incr;
				sum2 -= incr;

				if (total1 == 0 || sum1 == 0.0) {
					continue;
				} else if (total2 == 0 || sum2 == 0.0) {
					break;
				}

				double m1 = sum1 / total1;
				double m2 = sum2 / total2;

				double s1 = 0.0;
				double g = lv;
				for (int b = 0; b <= t; b++, g += bw) {
					double v = g - m1;
					s1 += counts[b] * v * v;
				}
				s1 /= total1;

				if (s1 < 0.5) {
					continue;
				}

				double s2 = 0.0;
				// g = lv;
				for (int b = t + 1; b < nBins; b++, g += bw) {
					double v = g - m2;
					s2 += counts[b] * v * v;
				}
				s2 /= total2;

				if (s2 < 0.5) {
					continue;
				}

				double P1 = total1 / total;
				double P2 = total2 / total;
				double J = 1.0 + P1 * Math.log(s1) + P2 * Math.log(s2) - 2.0
						* (P1 * Math.log(P1) + P2 * Math.log(P2));

				Jcount++;

				J0 = J1;
				J1 = J2;
				J2 = J;

				if (Jcount >= 3) {
					if (J1 <= J0 && J1 <= J2) {
						if (J1 < minCriterion) {
							minCriterion = J1;
							minIndex = t - 1;
							runLength = 0;
						} else if (J1 == minCriterion) {
							runLength++;
						}
					}
				}
			}

			thresholds[band] = minIndex == 0 ? mean[band] : getLowValue(band)
					+ (minIndex + runLength / 2.0 + 0.5) * bw;
		}

		return thresholds;
	}

	/**
	 * Calculates the threshold which minimizes the <i>fuzziness</i>.
	 * 
	 */
	public double[] getMinFuzzinessThreshold() {
		double[] thresholds = new double[numBands];
		getTotals();
		getMean();

		for (int band = 0; band < numBands; band++) {
			// Cache some band-dependent values.
			int nBins = numBins[band];
			double[] counts = getBins(band);
			double total = totals[band];

			double bw = binWidth[band];

			double total1 = 0;
			double total2 = totals[band];
			double sum1 = 0.0;
			double sum2 = mean[band] * total;

			double lv = getLowValue(band);
			double level = lv;
			double C = getHighValue(band) - lv;

			double minCriterion = Double.MAX_VALUE;
			int minIndex = 0;
			int runLength = 0;

			for (int t = 0; t < nBins; t++, level += bw) {
				double c = counts[t];

				total1 += c;
				total2 -= c;

				double incr = level * c;
				sum1 += incr;
				sum2 -= incr;

				if (total1 == 0 || total2 == 0) {
					continue;
				}

				double m1 = sum1 / total1;
				double m2 = sum2 / total2;

				double g = lv;
				double E = 0.0;
				for (int b = 0; b < nBins; b++, g += bw) {
					double u = b <= t ? 1.0 / (1.0 + Math.abs(g - m1) / C)
							: 1.0 / (1.0 + Math.abs(g - m2) / C);
					double v = 1 - u;
					E += (-u * Math.log(u) - v * Math.log(v))
							* (counts[b] / total);
				}

				if (E < minCriterion) {
					minCriterion = E;
					minIndex = t;
					runLength = 0;
				} else if (E == minCriterion) {
					runLength++;
				}
			}

			thresholds[band] = lv + (minIndex + runLength / 2.0 + 0.5) * bw;
		}

		return thresholds;
	}
}
