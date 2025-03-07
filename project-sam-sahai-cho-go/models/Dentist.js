const mongoose = require("mongoose");

const DentistSchema = new mongoose.Schema(
  {
    name: {
      type: String,
      required: [true, "Please add a name"],
      trim: true,
      maxlength: [50, "Name cannot be more than 50 characters"],
    },
    yearsOfExperience: {
      type: Number,
      required: [true, "Please add years of experience"],
      min: [0, "Experience cannot be negative"],
    },
    areaOfExpertise: {
      type: String,
      required: [true, "Please add an area of expertise"],
      trim: true,
    },
  },
  {
    toJSON: { virtuals: true },
    toObject: { virtuals: true },
  }
);

// Reverse populate with virtuals
// HospitalSchema.virtual('appointments', {
//   ref: 'Appointment',
//   localField: '_id',
//   foreignField: 'hospital',
//   justOne: false
// });

module.exports = mongoose.model("Dentist", DentistSchema);
