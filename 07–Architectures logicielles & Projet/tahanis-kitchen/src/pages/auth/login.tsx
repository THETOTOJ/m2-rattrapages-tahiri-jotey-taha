"use client";
import { supabase } from "@/lib/supabaseClient";
import { useRouter } from "next/navigation";
import { useEffect, useRef, useState } from "react";

export default function Login() {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [username, setUsername] = useState("");
  const [bio, setBio] = useState("");
  const [file, setFile] = useState<File | null>(null);
  const [preview, setPreview] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [isRegister, setIsRegister] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const fileInputRef = useRef<HTMLInputElement | null>(null);

  useEffect(() => {
    const checkUser = async () => {
      const {
        data: { user },
      } = await supabase.auth.getUser();
      if (user) router.replace("/");
    };
    checkUser();
  }, [router]);

  function validatePassword(pw: string) {
    const regex =
      /^(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]).{8,}$/;
    return regex.test(pw);
  }

  async function handleLogin() {
    setError(null);
    setSuccess(null);

    const { error } = await supabase.auth.signInWithPassword({
      email,
      password,
    });

    if (error) {
      setError(
        error.message.includes("Invalid login credentials")
          ? "Wrong email or password"
          : error.message
      );
    } else {
      setSuccess("Logged in successfully!");
      router.replace("/");
    }
  }

  async function handleForgotPassword() {
    if (!email) {
      setError("Enter your email first");
      return;
    }

    const { error } = await supabase.auth.resetPasswordForEmail(email, {
      redirectTo: `${window.location.origin}/auth/reset-password`,
    });

    if (error) setError(error.message);
    else setSuccess("Password reset email sent! Check your inbox.");
  }

  async function handleSignup() {
    setError(null);
    setSuccess(null);

    if (!username) {
      setError("Username is required");
      return;
    }
    if (!validatePassword(password)) {
      setError(
        "Password must be at least 8 characters, include one uppercase letter, one number, and one special character."
      );
      return;
    }
    if (password !== confirmPassword) {
      setError("Passwords do not match");
      return;
    }
    if (file && file.size > 2 * 1024 * 1024) {
      setError("Profile picture must be under 2MB");
      return;
    }

    try {
      // Step 1: Sign up the user
      const { data: signUpData, error: signUpError } =
        await supabase.auth.signUp({
          email,
          password,
          options: {
            data: { username },
            emailRedirectTo: `${window.location.origin}/auth/callback`,
          },
        });

      if (signUpError || !signUpData.user) {
        setError(signUpError?.message || "Signup failed");
        return;
      }

      // Step 2: Sign in the user to get proper session
      const { error: signInError } = await supabase.auth.signInWithPassword({
        email,
        password,
      });

      if (signInError) {
        setError("Failed to authenticate after signup");
        return;
      }

      // Step 3: Handle profile picture upload if provided
      let profile_picture: string | null = null;
      if (file) {
        const fileExt = file.name.split(".").pop();
        const filePath = `${signUpData.user.id}/avatar.${fileExt}`;

        const { data: imgData, error: imgError } = await supabase.storage
          .from("profile_pics")
          .upload(filePath, file, { upsert: true });

        if (imgError) {
          console.error("File upload error:", imgError);
          // Continue without profile picture instead of failing
          console.log("Continuing without profile picture...");
        } else {
          const {
            data: { publicUrl },
          } = supabase.storage.from("profile_pics").getPublicUrl(imgData.path);
          profile_picture = publicUrl;
        }
      }

      // Step 4: Create the user profile in the database
      const { error: profileError } = await supabase.from("users").insert({
        id: signUpData.user.id,
        email,
        username,
        profile_picture,
        bio,
      });

      if (profileError) {
        console.error("Profile creation error:", profileError);
        setError(`Profile setup failed: ${profileError.message}`);
        return;
      }

      // Success!
      setSuccess("Account created successfully! Check your email to confirm.");
      router.replace("/");
    } catch (error) {
      console.error("Unexpected error during signup:", error);
      setError("An unexpected error occurred. Please try again.");
    }
  }

  function handleFileChange(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0] || null;
    setFile(file);
    setPreview(file ? URL.createObjectURL(file) : null);
  }

  return (
    <div className="max-w-sm mx-auto mt-20 flex flex-col gap-4 bg-rose-50 p-6 rounded-xl shadow-md">
      <h1 className="text-2xl font-bold text-rose-800 mb-2 text-center">
        {isRegister ? "Register" : "Login"}
      </h1>

      {isRegister && (
        <div className="flex justify-center mb-2">
          <div
            className="w-24 h-24 rounded-full border-2 border-dashed border-rose-400 flex items-center justify-center cursor-pointer overflow-hidden bg-white hover:bg-rose-100 transition"
            onClick={() => fileInputRef.current?.click()}
          >
            {preview ? (
              <img
                src={preview}
                alt="Profile Preview"
                className="w-full h-full object-cover"
              />
            ) : (
              <span className="text-3xl text-rose-400">+</span>
            )}
          </div>
          <input
            ref={fileInputRef}
            type="file"
            accept="image/*"
            className="hidden"
            onChange={handleFileChange}
          />
        </div>
      )}

      <input
        type="email"
        placeholder="Email"
        className="border rounded px-3 py-2"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
      />

      <div className="relative">
        <input
          type={showPassword ? "text" : "password"}
          placeholder="Password"
          className="border rounded px-3 py-2 w-full pr-12"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <button
          type="button"
          className="absolute inset-y-0 right-3 flex items-center text-sm text-blue-600"
          onClick={() => setShowPassword((prev) => !prev)}
        >
          {showPassword ? "Hide" : "Show"}
        </button>
      </div>

      {isRegister && (
        <>
          <div className="relative">
            <input
              type={showConfirmPassword ? "text" : "password"}
              placeholder="Confirm Password"
              className="border rounded px-3 py-2 w-full pr-12"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
            />
            <button
              type="button"
              className="absolute inset-y-0 right-3 flex items-center text-sm text-blue-600"
              onClick={() => setShowConfirmPassword((prev) => !prev)}
            >
              {showConfirmPassword ? "Hide" : "Show"}
            </button>
          </div>

          <input
            type="text"
            placeholder="Username"
            className="border rounded px-3 py-2"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
          <textarea
            placeholder="Bio (optional)"
            className="border rounded px-3 py-2 resize-none"
            value={bio}
            onChange={(e) => setBio(e.target.value)}
          />
        </>
      )}

      {isRegister ? (
        <button
          onClick={handleSignup}
          className="bg-green-600 text-white py-2 rounded hover:bg-green-700 transition"
        >
          Sign Up
        </button>
      ) : (
        <button
          onClick={handleLogin}
          className="bg-rose-600 text-white py-2 rounded hover:bg-rose-700 transition"
        >
          Log In
        </button>
      )}

      <button
        onClick={() => setIsRegister(!isRegister)}
        className="text-sm text-blue-600 hover:underline"
      >
        {isRegister
          ? "Already have an account? Log in"
          : "Don't have an account? Register"}
      </button>

      {!isRegister && (
        <button
          onClick={handleForgotPassword}
          className="text-sm text-rose-600 hover:underline"
        >
          Forgot password?
        </button>
      )}

      {error && (
        <div className="bg-red-100 text-red-700 border border-red-300 px-3 py-2 rounded mt-2 text-sm">
          {error}
        </div>
      )}
      {success && (
        <div className="bg-green-100 text-green-700 border border-green-300 px-3 py-2 rounded mt-2 text-sm">
          {success}
        </div>
      )}
    </div>
  );
}
